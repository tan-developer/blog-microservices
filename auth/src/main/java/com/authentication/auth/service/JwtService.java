package com.authentication.auth.service;

import com.authentication.auth.dto.TokenDto;
import com.authentication.auth.entity.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {
    TokenDto generateToken(UserEntity username);

    TokenDto refreshToken(String refreshToken);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);

    String getEmailFromToken(String token);

    void addTokenCookies(TokenDto tokenDto);

    void clearCookies();

    String getAccessTokenFromCookies();

    String getRefreshTokenFromCookies();

    boolean refreshTokenFromCookies();
}