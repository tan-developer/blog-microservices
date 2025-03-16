package com.authentication.auth.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_entity")
@Data
@NoArgsConstructor
public class PasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String hashedPassword;
}
