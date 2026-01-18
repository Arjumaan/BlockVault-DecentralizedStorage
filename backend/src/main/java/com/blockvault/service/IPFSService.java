package com.blockvault.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Slf4j
public class IPFSService {

    @Value("${ipfs.host}")
    private String ipfsHost;

    @Value("${ipfs.port}")
    private int ipfsPort;

    @Value("${ipfs.protocol}")
    private String ipfsProtocol;

    private IPFS ipfs;

    @PostConstruct
    public void init() {
        try {
            ipfs = new IPFS("/ip4/" + ipfsHost + "/tcp/" + ipfsPort);
            log.info("Connected to IPFS node at {}://{}:{}", ipfsProtocol, ipfsHost, ipfsPort);

            // Test connection
            String version = ipfs.version();
            log.info("IPFS version: {}", version);
        } catch (Exception e) {
            log.error("Failed to connect to IPFS node: {}", e.getMessage());
            log.warn("IPFS features will not be available. Please start IPFS daemon.");
        }
    }

    /**
     * Upload file to IPFS
     * 
     * @param data     File data as byte array
     * @param filename Original filename
     * @return IPFS CID (Content Identifier)
     */
    public String uploadFile(byte[] data, String filename) throws IOException {
        if (ipfs == null) {
            throw new RuntimeException("IPFS connection not available. Please start IPFS daemon.");
        }

        try {
            NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(filename, data);
            MerkleNode response = ipfs.add(file).get(0);
            String cid = response.hash.toBase58();

            log.info("File uploaded to IPFS with CID: {}", cid);

            // Pin the file to ensure it stays in the network
            pinFile(cid);

            return cid;
        } catch (Exception e) {
            log.error("Error uploading file to IPFS: {}", e.getMessage());
            throw new IOException("Failed to upload file to IPFS: " + e.getMessage());
        }
    }

    /**
     * Download file from IPFS
     * 
     * @param cid IPFS Content Identifier
     * @return File data as byte array
     */
    public byte[] downloadFile(String cid) throws IOException {
        if (ipfs == null) {
            throw new RuntimeException("IPFS connection not available. Please start IPFS daemon.");
        }

        try {
            Multihash filePointer = Multihash.fromBase58(cid);
            byte[] data = ipfs.cat(filePointer);

            log.info("File downloaded from IPFS with CID: {}", cid);
            return data;
        } catch (Exception e) {
            log.error("Error downloading file from IPFS: {}", e.getMessage());
            throw new IOException("Failed to download file from IPFS: " + e.getMessage());
        }
    }

    /**
     * Pin file to ensure it stays in the IPFS network
     * 
     * @param cid IPFS Content Identifier
     */
    public void pinFile(String cid) {
        if (ipfs == null) {
            log.warn("Cannot pin file - IPFS not connected");
            return;
        }

        try {
            Multihash filePointer = Multihash.fromBase58(cid);
            ipfs.pin.add(filePointer);
            log.info("File pinned: {}", cid);
        } catch (Exception e) {
            log.error("Error pinning file: {}", e.getMessage());
        }
    }

    /**
     * Unpin file from IPFS (allows garbage collection)
     * 
     * @param cid IPFS Content Identifier
     */
    public void unpinFile(String cid) {
        if (ipfs == null) {
            log.warn("Cannot unpin file - IPFS not connected");
            return;
        }

        try {
            Multihash filePointer = Multihash.fromBase58(cid);
            ipfs.pin.rm(filePointer);
            log.info("File unpinned: {}", cid);
        } catch (Exception e) {
            log.error("Error unpinning file: {}", e.getMessage());
        }
    }

    /**
     * Check if IPFS is connected
     */
    public boolean isConnected() {
        return ipfs != null;
    }

    /**
     * Get IPFS node info
     */
    public String getNodeInfo() {
        if (ipfs == null) {
            return "IPFS not connected";
        }

        try {
            return ipfs.version();
        } catch (Exception e) {
            return "Error getting IPFS info";
        }
    }
}
