package com.authentication.auth.service.implement;

import com.authentication.auth.dto.TokenDto;
import com.authentication.auth.entity.user.UserEntity;
import com.authentication.auth.exception.BusinessCode;
import com.authentication.auth.exception.BusinessException;
import com.authentication.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtServiceImplement implements JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // Cookie configuration properties
    @Value("${application.security.jwt.cookie.access-token-name:access_token}")
    private String accessTokenCookieName;

    @Value("${application.security.jwt.cookie.refresh-token-name:refresh_token}")
    private String refreshTokenCookieName;

    @Value("${application.security.jwt.cookie.domain:}")
    private String domain;

    @Value("${application.security.jwt.cookie.secure:true}")
    private boolean secure;

    @Value("${application.security.jwt.cookie.http-only:true}")
    private boolean httpOnly;

    @Value("${application.security.jwt.cookie.path:/}")
    private String path;

    @Value("${application.security.jwt.kid:key}")
    private String kid;

    @Override
    public TokenDto generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();

        String accessToken = createToken(claims, user, jwtExpiration);

        claims.put("token_type", "refresh");
        String refreshToken = createToken(claims, user, refreshExpiration);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenDto refreshToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            String tokenType = claims.get("token_type", String.class);


            if (!"refesh".equals(tokenType)) {
                throw new RuntimeException("Invalid refresh token");
            }

            TokenDto tokenDto = getTokenFromCookies().orElseThrow(() -> new BusinessException(BusinessCode.UNAUTH));


            String username = getUsernameFromToken(tokenDto.getAccessToken());

            Claims claimsAccessToken = extractAllClaims(tokenDto.getAccessToken());

            UserEntity user = UserEntity.builder()
                    .userId(claimsAccessToken.get("userId", String.class))
                    .username(username)
                    .email(claimsAccessToken.get("email", String.class))
                    .firstName(claimsAccessToken.get("firstName", String.class))
                    .lastName(claimsAccessToken.get("lastName", String.class))
                    .build();


            return generateToken(user);

        } catch (ExpiredJwtException ex) {
            throw new BusinessException(BusinessCode.REFRESH_EXPRIED);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }



    @Override
    public void addTokenCookies(TokenDto tokenDto) {
        addCookie(
                accessTokenCookieName,
                tokenDto.getAccessToken(),
                (int) (jwtExpiration / 1000)
        );

        addCookie(
                refreshTokenCookieName,
                tokenDto.getRefreshToken(),
                (int) (refreshExpiration / 1000)
        );
    }

    @Override
    public void clearCookies() {
        addCookie(accessTokenCookieName, "", 0);
        addCookie(refreshTokenCookieName, "", 0);
    }

    @Override
    public String getAccessTokenFromCookies() {
        Cookie cookie = WebUtils.getCookie(getRequest(), accessTokenCookieName);
        return cookie != null ? cookie.getValue() : null;
    }

    @Override
    public String getRefreshTokenFromCookies() {
        Cookie cookie = WebUtils.getCookie(getRequest(), refreshTokenCookieName);
        return cookie != null ? cookie.getValue() : null;
    }

    @Override
    public boolean refreshTokenFromCookies() {
        String refreshToken = getRefreshTokenFromCookies();

        if (refreshToken == null) {
            return false;
        }
        try {
            TokenDto tokenDto = refreshToken(refreshToken);
            addTokenCookies(tokenDto);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private String createToken(Map<String, Object> claims, UserEntity subject, long expiration) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "jwt");
        headers.put("alg", "HS256");
        headers.put("kid", kid);

        claims.put("username", subject.getUsername());
        claims.put("email", subject.getEmail());
        claims.put("firstName", subject.getFirstName());
        claims.put("lastName", subject.getLastName());
        claims.put("userId", subject.getUserId());

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setSubject(subject.getUsername()) // Keep subject as username
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String getEmailFromToken(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }


    private void addCookie(String name, String value, int maxAgeSecs) {
        HttpServletResponse response = getResponse();


        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(domain)
                .path(path)
                .maxAge(maxAgeSecs)
                .httpOnly(httpOnly)
                .secure(secure)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public HttpServletResponse getResponse () {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;

        return attributes.getResponse();
    }

    public HttpServletRequest getRequest () {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;

        return attributes.getRequest();
    }

    public Optional<TokenDto> getTokenFromCookies() {
        HttpServletRequest request = getRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        String accessToken = null;
        String refreshToken = null;

        for (Cookie cookie : cookies) {
            if (accessTokenCookieName.equals(cookie.getName())) {
                accessToken = cookie.getValue();
            } else if (refreshTokenCookieName.equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        if (accessToken != null) {
            TokenDto tokenDto = TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return Optional.ofNullable(tokenDto);
        }

        return Optional.empty();
    }
}