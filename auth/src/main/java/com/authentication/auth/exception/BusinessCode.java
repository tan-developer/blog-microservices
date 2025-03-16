package com.authentication.auth.exception;

import com.authentication.auth.base.model.BaseResonse;
import org.springframework.http.HttpStatus;

public class BusinessCode {
    public static final BaseResonse USER_ALREADY_EXITS =
            BaseResonse.error("EMAIL_ALREADY_EXITS" , HttpStatus.CONFLICT);

    public static final BaseResonse INVALID_CREDENTIALS =
            BaseResonse.error("Invalid credentials" , HttpStatus.UNAUTHORIZED);

    public static final BaseResonse USER_INACTIVE =
            BaseResonse.error("User inactive" , HttpStatus.UNAUTHORIZED);

    public static final BaseResonse USERNAME_NOT_FOUND =
            BaseResonse.error("Invalid credentials" , HttpStatus.UNAUTHORIZED);

    public static final BaseResonse UNAUTH =
            BaseResonse.error("Invalid credentials" , HttpStatus.UNAUTHORIZED);


    public static final BaseResonse REFRESH_EXPRIED =
            BaseResonse.error("Invalid credentials" , HttpStatus.UNAUTHORIZED);
}
