package com.example.budgettracker.controller;

// Update this import
import com.example.budgettracker.security.JwtService;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;  // Add this import
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName()
        );
        // Fix: Remove explicit casting and use the user object directly
        String token = jwtService.generateToken(Map.of("roles", user.getRoles()), user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(Map.of("roles", user.getRoles()), user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    record LoginRequest(String email, String password) {}
    record RegisterRequest(String email, String password, String firstName, String lastName) {}
    record AuthResponse(String token) {}
}