package com.blockvault.service;

import com.blockvault.model.FileMetadata;
import com.blockvault.model.User;
import com.blockvault.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final FileMetadataRepository fileMetadataRepository;

    /**
     * Get storage statistics for user
     */
    public Map<String, Object> getStorageStats(User user) {
        Map<String, Object> stats = new HashMap<>();

        Long totalStorage = user.getStorageQuota();
        Long usedStorage = user.getUsedStorage();
        Long availableStorage = totalStorage - usedStorage;

        stats.put("totalStorage", totalStorage);
        stats.put("usedStorage", usedStorage);
        stats.put("availableStorage", availableStorage);
        stats.put("usagePercentage", calculatePercentage(usedStorage, totalStorage));

        stats.put("totalStorageFormatted", formatBytes(totalStorage));
        stats.put("usedStorageFormatted", formatBytes(usedStorage));
        stats.put("availableStorageFormatted", formatBytes(availableStorage));

        long fileCount = fileMetadataRepository.countByUserId(user.getId());
        stats.put("fileCount", fileCount);

        stats.put("activeNodes", 3);
        stats.put("replicationFactor", 3);
        stats.put("pinnedFiles", fileCount);

        return stats;
    }

    /**
     * Get recent activity feed
     */
    public List<Map<String, Object>> getActivityFeed(User user) {
        List<Map<String, Object>> activities = new ArrayList<>();

        List<FileMetadata> recentFiles = fileMetadataRepository.findTop4ByUserIdOrderByUploadedAtDesc(user.getId());

        for (FileMetadata file : recentFiles) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("timestamp", formatTimestamp(file.getUploadedAt()));
            activity.put("action", "UPLOAD");
            activity.put("detail", file.getFilename());
            activity.put("cid", shortenCid(file.getCid()));
            activity.put("node", "Local-Node");
            activities.add(activity);
        }

        return activities;
    }

    /**
     * Get analytics data
     */
    public Map<String, Object> getAnalytics(User user) {
        Map<String, Object> analytics = new HashMap<>();

        List<FileMetadata> allFiles = fileMetadataRepository.findByUserId(user.getId());

        // Files by month
        Map<String, Integer> filesByMonth = new HashMap<>();
        for (FileMetadata file : allFiles) {
            String month = file.getUploadedAt().format(DateTimeFormatter.ofPattern("MMM"));
            filesByMonth.put(month, filesByMonth.getOrDefault(month, 0) + 1);
        }
        analytics.put("filesByMonth", filesByMonth);

        // Files by type
        Map<String, Integer> filesByType = new HashMap<>();
        for (FileMetadata file : allFiles) {
            String type = file.getFileType();
            filesByType.put(type, filesByType.getOrDefault(type, 0) + 1);
        }
        analytics.put("filesByType", filesByType);

        // Total uploads this month
        long thisMonthUploads = allFiles.stream()
                .filter(f -> f.getUploadedAt().getMonth() == LocalDateTime.now().getMonth())
                .count();
        analytics.put("thisMonthUploads", thisMonthUploads);

        // Encrypted files count
        long encryptedCount = allFiles.stream().filter(FileMetadata::getEncrypted).count();
        analytics.put("encryptedFiles", encryptedCount);

        return analytics;
    }

    // Helper methods

    private double calculatePercentage(long used, long total) {
        if (total == 0)
            return 0.0;
        return (double) used / total * 100.0;
    }

    private String formatBytes(long bytes) {
        if (bytes >= 1_073_741_824) {
            return String.format("%.2f GB", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576) {
            return String.format("%.2f MB", bytes / 1_048_576.0);
        } else if (bytes >= 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return bytes + " B";
        }
    }

    private String formatTimestamp(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(timestamp, now).toMinutes();

        if (minutes < 1)
            return "just now";
        if (minutes < 60)
            return minutes + " min ago";

        long hours = minutes / 60;
        if (hours < 24)
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";

        long days = hours / 24;
        if (days == 1)
            return "Yesterday";
        if (days < 7)
            return days + " days ago";

        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    private String shortenCid(String cid) {
        if (cid == null || cid.length() < 8)
            return cid;
        return cid.substring(0, 4) + "..." + cid.substring(cid.length() - 4);
    }
}
