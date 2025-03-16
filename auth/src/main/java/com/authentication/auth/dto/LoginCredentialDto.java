package com.authentication.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginCredentialDto {
    private String email;
    private String password;
}
