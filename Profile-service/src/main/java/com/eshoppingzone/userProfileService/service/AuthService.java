package com.eshoppingzone.userProfileService.service;

import com.eshoppingzone.userProfileService.entity.Role;
import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.payload.request.LoginRequest;
import com.eshoppingzone.userProfileService.payload.request.SignupRequest;
import com.eshoppingzone.userProfileService.payload.response.JwtResponse;
import com.eshoppingzone.userProfileService.payload.response.MessageResponse;
import com.eshoppingzone.userProfileService.repository.UserRepository;
import com.eshoppingzone.userProfileService.security.jwt.JwtUtils;
import com.eshoppingzone.userProfileService.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Users user = new Users();
        user.setFullName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setMobile(signUpRequest.getMobile());
        user.setAddress(signUpRequest.getAddress());

        Set<Role> roles = new HashSet<>();
        if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
            roles.add(Role.ROLE_USER);
        } else {
            signUpRequest.getRoles().forEach(role -> {
                switch (role) {
                    case "admin" -> roles.add(Role.ROLE_ADMIN);
                    case "merchant" -> roles.add(Role.ROLE_MERCHANT);
                    default -> roles.add(Role.ROLE_USER);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> validateSession(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            boolean isValid = jwtUtils.validateJwtToken(jwt);

            if (isValid) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                Users user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    return ResponseEntity.ok(Map.of(
                            "isLoggedIn", true,
                            "userId", user.getId(),
                            "username", user.getUsername()
                    ));
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "isLoggedIn", false,
                "message", "User is not logged in"
        ));
    }
}
