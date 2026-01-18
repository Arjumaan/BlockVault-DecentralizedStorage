package com.blockvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlockVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlockVaultApplication.class, args);
        System.out.println("\n" +
                "╔══════════════════════════════════════════════════════════╗\n" +
                "║                                                          ║\n" +
                "║         BlockVault Backend - Successfully Started        ║\n" +
                "║                                                          ║\n" +
                "║  🚀 Server running on: http://localhost:8080            ║\n" +
                "║  📊 H2 Console: http://localhost:8080/h2-console        ║\n" +
                "║  🔒 Authentication: JWT-based                            ║\n" +
                "║  🌐 IPFS Integration: Active                             ║\n" +
                "║                                                          ║\n" +
                "╚══════════════════════════════════════════════════════════╝\n");
    }
}
