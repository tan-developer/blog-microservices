package com.authentication.auth.controller;


import com.authentication.auth.base.model.BaseResonse;
import com.authentication.auth.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@AllArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiControllerException {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResonse<?>> handleBusinessException(BusinessException e){
        return ResponseEntity.status(e.getInstance().getCode()).body(e.getInstance());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleException(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResonse.error(e.getMessage() , HttpStatus.BAD_REQUEST));
    }
}
