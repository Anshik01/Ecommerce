package com.eshoppingzone.userProfileService.service;

import com.eshoppingzone.userProfileService.entity.Users;

public interface UserService {
    Users getCurrentUserProfile();
    Users updateUserProfile(Users updatedUserDetails);
}
