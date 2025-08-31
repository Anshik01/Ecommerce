package com.eshoppingzone.userProfileService.controller;

import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers() {
        logger.info("Fetching all users");
        List<Users> users = adminService.getAllUsers();
        logger.debug("Total users retrieved: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        logger.info("Fetching user with ID: {}", id);
        Users user = adminService.getUserById(id);
        if (user == null) {
            logger.warn("User not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);

    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        logger.info("Attempting to delete user with ID: {}", id);
        return ResponseEntity.ok(adminService.deleteUser(id));
    }

}


//    @PutMapping("/users/{id}/promote")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> promoteToAdmin(@PathVariable int id) {
//        return ResponseEntity.ok(adminService.promoteToAdmin(id));
//    }