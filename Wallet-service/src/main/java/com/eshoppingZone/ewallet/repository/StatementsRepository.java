package com.eshoppingZone.ewallet.repository;

import com.eshoppingZone.ewallet.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatementsRepository extends JpaRepository<Statement,Integer> {
}
