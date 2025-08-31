package com.eshoppingzone.userProfileService.service;

import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.payload.response.MessageResponse;

import java.util.List;

public interface AdminService {

    List<Users> getAllUsers();
    Users getUserById(int id);
    MessageResponse deleteUser(int id);

}
