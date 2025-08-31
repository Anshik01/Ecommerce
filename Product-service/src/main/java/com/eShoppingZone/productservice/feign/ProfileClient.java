package com.eShoppingZone.productservice.feign;


import com.eShoppingZone.productservice.dto.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="PROFILE-SERVICE")
public interface ProfileClient {

@GetMapping("/api/users/profile")
public ResponseEntity<UserProfile> getUserProfile(@RequestHeader("Authorization") String token) ;

}
