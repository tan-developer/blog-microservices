package com.authentication.auth.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date createdAt;

    private Date updatedAt;

    private boolean status;

    private boolean isVerified;

    private boolean isRequiredOtp;

    private boolean isThirdPartyLogin;

    private Date dateOfBirth;

    @ManyToOne(targetEntity = PasswordEntity.class)
    private PasswordEntity passwordEntity;
}
