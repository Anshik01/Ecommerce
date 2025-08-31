package com.order.orderservice.repository;

import com.order.orderservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  AddressRepository extends JpaRepository<Address,Integer> {
      Address findByUserId(int id);

}
