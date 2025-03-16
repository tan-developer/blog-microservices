package com.authentication.auth.repository;

import com.authentication.auth.entity.user.PasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PasswordEntityRepository extends JpaRepository<PasswordEntity, String> {
}
