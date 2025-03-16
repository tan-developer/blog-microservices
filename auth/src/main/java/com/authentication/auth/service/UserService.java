package com.authentication.auth.service;

import com.authentication.auth.dto.CreateUserDTO;
import com.authentication.auth.entity.user.UserEntity;

public interface UserService {
    public UserEntity createUser(CreateUserDTO payload);
    public UserEntity updateUser();
    public UserEntity deleteUser();

    public UserEntity getUser();
    public UserEntity getUserById(String id);
    public UserEntity getUserByEmail(String email);
    public UserEntity getUserByUsername(String username);
    public UserEntity getUsers();
}
