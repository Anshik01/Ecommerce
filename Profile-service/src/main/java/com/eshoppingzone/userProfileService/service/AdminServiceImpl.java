package com.eshoppingzone.userProfileService.service;

import com.eshoppingzone.userProfileService.entity.Role;
import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.payload.response.MessageResponse;
import com.eshoppingzone.userProfileService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Users> getAllUsers() {
        List<Users> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public Users getUserById(int id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setPassword(null);
        return user;
    }


    @Override
    public MessageResponse deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            return new MessageResponse("Error: User not found");
        }

        userRepository.deleteById(id);
        return new MessageResponse("User deleted successfully");
    }

}

//    @Override
//    public MessageResponse setupFirstAdmin(String username) {
//        boolean adminExists = userRepository.findAll().stream()
//                .anyMatch(user -> user.getRoles().contains(Role.ROLE_ADMIN));
//
//        if (adminExists) {
//            return new MessageResponse("Error: Admin already exists. This endpoint is only for initial setup.");
//        }
//
//        Users user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
//
//        Set<Role> roles = new HashSet<>(user.getRoles());
//        roles.add(Role.ROLE_ADMIN);
//        user.setRoles(roles);
//        userRepository.save(user);
//
//        return new MessageResponse("First admin user created successfully");
//    }



//@Override
//public MessageResponse promoteToAdmin(int id) {
//    Users user = userRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
//
//    Set<Role> roles = user.getRoles();
//    roles.add(Role.ROLE_ADMIN);
//    user.setRoles(roles);
//    userRepository.save(user);
//
//    return new MessageResponse("User promoted to ADMIN successfully");
//}