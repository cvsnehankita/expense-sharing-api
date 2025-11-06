package com.expense.api.controller;

import com.expense.api.dto.AuthResponse;
import com.expense.api.dto.LoginRequest;
import com.expense.api.dto.RegisterRequest;
import com.expense.api.entity.CustomUserDetails;
import com.expense.api.entity.Role;
import com.expense.api.entity.User;
import com.expense.api.service.UserService;
import com.expense.api.utils.JWTUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("User trying to register with email: {}", registerRequest.getEmail());

        // Check if user already exists
        Optional<User> existingUser = userService.findByUserEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        // Create new user
        User newUser = User.builder()
                .userName(registerRequest.getName())
                .userEmail(registerRequest.getEmail())
                .userPassword(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();

        User savedUser = userService.saveUser(newUser);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getUserEmail(), savedUser.getRole().name());

        AuthResponse response = new AuthResponse(
                token,
                savedUser.getUserEmail(),
                savedUser.getUserName(),
                savedUser.getRole().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("User trying to login with email: {}", loginRequest.getEmail());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Get user details
            User user = userService.findByUserEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUserEmail(), user.getRole().name());

            AuthResponse response = new AuthResponse(
                    token,
                    user.getUserEmail(),
                    user.getUserName(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User: " + user.getUserName());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("name", user.getUserName());
        userInfo.put("email", user.getUserEmail());
        userInfo.put("role", user.getRole());

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauthSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 authentication failed");
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Google login successful!");
        response.put("email", email);
        response.put("name", name);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to Expense Sharing API!");
    }
}
