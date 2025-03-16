package com.authentication.auth.repository;

import com.authentication.auth.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity , String> {
    Optional<UserEntity> findByEmail(String email);
}
