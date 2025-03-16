package com.authentication.auth.exception;

import com.authentication.auth.base.model.BaseResonse;
import org.springframework.http.HttpStatus;

public class BusinessCode {
    public static final BaseResonse USER_ALREADY_EXITS =
            BaseResonse.error("EMAIL_ALREADY_EXITS" , HttpStatus.CONFLICT);
}
