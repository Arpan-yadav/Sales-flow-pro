package com.sales.controller;

import com.sales.dto.*;
import com.sales.model.User;
import com.sales.security.JwtUtil;
import com.sales.security.UserDetailsImpl;
import com.sales.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        String token = jwtUtil.generateToken(
            userDetails, 
            userDetails.getId(), 
            userDetails.getAuthorities().iterator().next().getAuthority()
        );

        AuthResponse resp = AuthResponse.builder()
            .token(token)
            .email(userDetails.getUsername())
            .role(userDetails.getAuthorities().iterator().next().getAuthority())
            .name(userDetails.getUsername()) // Simplified for now
            .build();

        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getMe(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getById(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterRequest request) {
        UserService.UserRequest req = new UserService.UserRequest();
        req.setName(request.getName());
        req.setEmail(request.getEmail());
        req.setPassword(request.getPassword());
        req.setRole(request.getRole());
        req.setPhone(request.getPhone());

        User user = userService.create(req);
        return ResponseEntity.ok(ApiResponse.success("User registered", user));
    }
}
