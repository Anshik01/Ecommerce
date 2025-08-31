package com.cartservice.service;

import com.cartservice.exception.UnauthorizedException;
import com.cartservice.feign.ProfileServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecurityService {

    @Autowired
    private ProfileServiceClient profileServiceClient;
    
    public boolean validateToken(String token) {
        try {
            ResponseEntity<Map<String, Object>> response = profileServiceClient.validateSession(token);
            return Boolean.TRUE.equals(response.getBody().get("isLoggedIn"));
        } catch (Exception e) {
            return false;
        }
    }

    public int getUserIdFromToken(String token) {
        try {
            ResponseEntity<Map<String, Object>> response = profileServiceClient.validateSession(token);
            Map<String, Object> body = response.getBody();

            if (body == null || !Boolean.TRUE.equals(body.get("isLoggedIn"))) {
                throw new UnauthorizedException("Invalid or expired token");
            }

            return ((Number) body.get("userId")).intValue();
        } catch (Exception e) {
            throw new UnauthorizedException("Error validating token: " + e.getMessage());
        }
    }
}
