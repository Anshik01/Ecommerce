package com.eShoppingZone.productservice.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class UserProfile {

    private int id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String mobile;
    private String address;
    private Set<Role> roles = new HashSet<>();

    private String githubUsername;

    private boolean isActive = true;
}
