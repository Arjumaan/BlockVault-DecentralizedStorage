package com.blockvault.controller;

import com.blockvault.model.FileMetadata;
import com.blockvault.model.User;
import com.blockvault.service.AuthService;
import com.blockvault.service.DashboardService;
import com.blockvault.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final FileService fileService;
    private final AuthService authService;

    /**
     * Get dashboard statistics
     * GET /api/dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            Map<String, Object> stats = dashboardService.getStorageStats(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", stats));
        } catch (Exception e) {
            log.error("Get stats failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Get recent files for dashboard
     * GET /api/dashboard/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentFiles(Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            List<FileMetadata> recentFiles = fileService.getRecentFiles(user);

            List<Map<String, Object>> filesResponse = recentFiles.stream()
                    .map(this::convertToFileResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "files", filesResponse));
        } catch (Exception e) {
            log.error("Get recent files failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Get activity feed
     * GET /api/dashboard/activity
     */
    @GetMapping("/activity")
    public ResponseEntity<?> getActivity(Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            List<Map<String, Object>> activities = dashboardService.getActivityFeed(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "activities", activities));
        } catch (Exception e) {
            log.error("Get activity failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    // Helper methods

    private Map<String, Object> convertToFileResponse(FileMetadata file) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", file.getId());
        response.put("name", file.getFilename());
        response.put("cid", file.getCid());
        response.put("size", file.getFormattedSize());
        response.put("type", file.getFileType());
        response.put("encrypted", file.getEncrypted());
        response.put("icon", file.getFileIcon());
        response.put("date", file.getUploadedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        return response;
    }
}
