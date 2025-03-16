package com.authentication.auth.controller;

import com.authentication.auth.base.model.BaseResonse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseResponseController {
    public <T> ResponseEntity success(T data) {
        return ResponseEntity.ok(BaseResonse.success(data));
    }

    public <T> ResponseEntity created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResonse.created(data));
    }
}
