package com.example.userservice.service;

import com.example.userservice.dto.CountDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();

    UserDto deleteByUserId(String userId);

    void createCount(String userId, String musicId);

    CountDto findByUserIdAndMusic(String userId, String productId);

    UserDto patchByUserId(String userId, UserDto userDto);
}
