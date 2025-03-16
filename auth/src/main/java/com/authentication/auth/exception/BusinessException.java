package com.authentication.auth.exception;

import com.authentication.auth.base.model.BaseResonse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BusinessException extends RuntimeException{
    private final BaseResonse<?> instance;
}
