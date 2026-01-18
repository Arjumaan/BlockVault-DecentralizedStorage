package com.blockvault.controller;

import com.blockvault.model.User;
import com.blockvault.service.AuthService;
import com.blockvault.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final DashboardService dashboardService;
    private final AuthService authService;

    /**
     * Get analytics data
     * GET /api/analytics
     */
    @GetMapping
    public ResponseEntity<?> getAnalytics(Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            Map<String, Object> analytics = dashboardService.getAnalytics(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "analytics", analytics));
        } catch (Exception e) {
            log.error("Get analytics failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }
}
