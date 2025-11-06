package com.expense.api.controller;

import com.expense.api.dto.JwtResponse;
import com.expense.api.dto.LoginRequest;
import com.expense.api.entity.CustomUserDetails;
import com.expense.api.entity.Role;
import com.expense.api.entity.User;
import com.expense.api.service.UserService;
import com.expense.api.utils.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService; //
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
          // SecurityContextHolder.getContext().setAuthentication(auth);

            // Optional: generate JWT
           /// String token = jwtService.generateToken(auth);
            //return ResponseEntity.ok(Map.of("token", token));

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "userId", user.getUserId(),
                    "email", user.getUserEmail()
            ));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/login1")
    public JwtResponse login1(@Valid @RequestBody LoginRequest request) {

        // Authenticate user (username/password)
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Generate JWT token
        String token = jwtService.generateToken(authentication);

        return new JwtResponse(token, "Bearer");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        Optional<User> exsistingUser = userService.findByUserEmail(user.getUserEmail());
        System.out.println("existingu: " + exsistingUser);
        if (!exsistingUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setUserEmail(user.getUserEmail());
        newUser.setUserPassword(passwordEncoder.encode(user.getUserPassword())); // encode password
        newUser.setEnabled(true);
        newUser.setRole(Role.USER);

        userService.saveUser(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/login-success")
    public Map<String, String> loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Google login successful!");
        if (oauth2User != null) {
            response.put("name", oauth2User.getAttribute("name"));
            response.put("email", oauth2User.getAttribute("email"));
        }
        return response;
    }

}
