package com.eshoppingzone.userProfileService.controller;

import com.eshoppingzone.userProfileService.payload.request.LoginRequest;
import com.eshoppingzone.userProfileService.payload.request.SignupRequest;
import com.eshoppingzone.userProfileService.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }

    @GetMapping("/validate-session")
    public ResponseEntity<?> validateSession(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return authService.validateSession(authHeader);
    }
}
