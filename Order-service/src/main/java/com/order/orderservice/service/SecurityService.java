package com.order.orderservice.service;

import com.order.orderservice.dto.UserProfile;
import com.order.orderservice.exception.UnauthorizedException;
import com.order.orderservice.feign.ProfileServiceClient;
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


    public boolean isMerchantOrAdmin(String token){
        ResponseEntity<UserProfile> profileResponse = profileServiceClient.getUserProfile(token);
        if (profileResponse.getStatusCode().is2xxSuccessful() && profileResponse.getBody() != null) {
            UserProfile userProfile = profileResponse.getBody();
            for (Object role : userProfile.getRoles()) {
                if (role.toString().equalsIgnoreCase("ROLE_ADMIN") || role.toString().equalsIgnoreCase("ROLE_MERCHANT")) {
                    System.out.println("Admin/Merchant role found in id " + userProfile.getId() + " with name " + userProfile.getFullName());
                    return true;
                }
            }
        }
        System.out.println("Wasn't able to find the admin/merchant role");
        return false;
    }
}
