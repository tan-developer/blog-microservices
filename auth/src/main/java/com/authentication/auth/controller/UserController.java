package com.authentication.auth.controller;


import com.authentication.auth.base.model.BaseResonse;
import com.authentication.auth.constant.Route;
import com.authentication.auth.dto.CreateUserDTO;
import com.authentication.auth.dto.LoginCredentialDto;
import com.authentication.auth.entity.user.UserEntity;
import com.authentication.auth.service.implement.UserServiceImplement;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController extends BaseResponseController {

    protected UserServiceImplement userServiceImplement;

    public UserController(UserServiceImplement userServiceImplement) {
        this.userServiceImplement = userServiceImplement;
    }


    @PostMapping(Route.REGISTER)
    private ResponseEntity<BaseResonse<UserEntity>> createUser(
            @NotNull @RequestBody CreateUserDTO payload
    ){
        return created(userServiceImplement.createUser(payload));
    }

    @PostMapping(Route.AUTH)
    private ResponseEntity<BaseResonse<String>> loginCredentials(
            @NotNull @RequestBody LoginCredentialDto payload
    ){
        return success(userServiceImplement.loginCredentials(payload));
    }
}
