package com.blockvault.service;

import com.blockvault.model.User;
import com.blockvault.repository.UserRepository;
import com.blockvault.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${blockvault.storage.default-quota}")
    private Long defaultStorageQuota;

    /**
     * Register a new user
     */
    public User register(String username, String email, String password) throws Exception {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required");
        }
        if (password == null || password.length() < 6) {
            throw new Exception("Password must be at least 6 characters");
        }

        // Check if username exists
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(email)) {
            throw new Exception("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStorageQuota(defaultStorageQuota);
        user.setUsedStorage(0L);

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", username);

        return savedUser;
    }

    /**
     * Authenticate user and generate JWT token
     */
    public String login(String username, String password) throws Exception {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            if (authentication.isAuthenticated()) {
                // Generate JWT token
                String token = jwtUtil.generateToken(username);
                log.info("User logged in successfully: {}", username);
                return token;
            } else {
                throw new Exception("Authentication failed");
            }
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", username, e.getMessage());
            throw new Exception("Invalid username or password");
        }
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));
    }
}
