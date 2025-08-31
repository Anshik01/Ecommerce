package com.eShoppingZone.productservice.service;

import com.eShoppingZone.productservice.dto.UserProfile;
import com.eShoppingZone.productservice.exception.ResourceNotFoundException;
import com.eShoppingZone.productservice.feign.ProfileClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    ProfileClient profileClient;

   public boolean isMerchantOrAdmin(String token){
       ResponseEntity<UserProfile> profileResponse = profileClient.getUserProfile(token);
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
