package com.example.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface CountRepository extends CrudRepository<CountEntity, Long> {

    CountEntity findByUserIdAndMusic(String userId, String musicId);
}
