package com.authentication.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Slf4j
@AllArgsConstructor
public class CreateUserDTO {
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String unHashedPassword;
    private Date dateOfBirth;
}
