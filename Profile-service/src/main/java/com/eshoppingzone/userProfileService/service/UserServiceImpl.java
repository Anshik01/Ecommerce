package com.eshoppingzone.userProfileService.service;

import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.repository.UserRepository;
import com.eshoppingzone.userProfileService.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users getCurrentUserProfile() {
        int userId = getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(null);
        return user;
    }

    @Override
    public Users updateUserProfile(Users updatedUserDetails) {
        int userId = getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(updatedUserDetails.getFullName());
        user.setMobile(updatedUserDetails.getMobile());
        user.setAddress(updatedUserDetails.getAddress());

        Users updatedUser = userRepository.save(user);
        updatedUser.setPassword(null);
        return updatedUser;
    }

    private int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
