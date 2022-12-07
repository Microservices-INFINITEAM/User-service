package com.example.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface CountRepository extends CrudRepository<CountEntity, Long> {

    CountEntity findByUserIdAndProduct(String userId, String productId);
}
