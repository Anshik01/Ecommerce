package com.eshoppingZone.ewallet.repository;

import com.eshoppingZone.ewallet.entity.Ewallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EwalletRepository extends JpaRepository<Ewallet,Integer> {
    Optional<Ewallet> findById(int id);
    Optional<Ewallet> findByUserId(int userId);
}
