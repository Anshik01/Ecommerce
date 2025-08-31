package com.eshoppingZone.ewallet.feign;

import com.eshoppingZone.ewallet.dto.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "profile-service")
public interface ProfileServiceClient {

    @GetMapping("/api/auth/validate-session")
    ResponseEntity<Map<String, Object>> validateSession(@RequestHeader("Authorization") String token);

    @GetMapping("/api/users/profile")
    public ResponseEntity<UserProfile> getUserProfile(@RequestHeader("Authorization") String token) ;

}
