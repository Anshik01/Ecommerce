package com.eshoppingzone.userProfileService.repository;

import com.eshoppingzone.userProfileService.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Integer> {

    Optional<Users> findByUsername(String userName);
    Optional<Users> findByEmail(String email);
    Boolean existsByUsername(String userName);
    Boolean existsByEmail(String email);

}

