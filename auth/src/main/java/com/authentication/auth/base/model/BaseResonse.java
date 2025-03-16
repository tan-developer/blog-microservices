package com.authentication.auth.base.model;

import com.authentication.auth.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class BaseResonse<T> {
    private String message;
    private HttpStatus code;
    private T data;


    public static <T> BaseResonse<T> success(T data) {
        return new BaseResonse<>(null , HttpStatus.OK ,data);
    }

    public static <T> BaseResonse<T> created(T data) {
        return new BaseResonse<>(null , HttpStatus.CREATED,data);
    }

    public static <T> BaseResonse<T> error(BusinessException exception) {
        BaseResonse<?> mutationException = exception.getInstance();
        return new BaseResonse<>(mutationException.message , mutationException.code , null);
    }

    public static BaseResonse error(String message , HttpStatus code) {
        return new BaseResonse<>(message , code , null);
    }
}
