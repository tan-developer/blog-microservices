package com.authentication.auth.service.implement;

import com.authentication.auth.dto.CreateUserDTO;
import com.authentication.auth.dto.LoginCredentialDto;
import com.authentication.auth.dto.TokenDto;
import com.authentication.auth.entity.user.PasswordEntity;
import com.authentication.auth.entity.user.UserEntity;
import com.authentication.auth.exception.BusinessException;
import com.authentication.auth.exception.BusinessCode;
import com.authentication.auth.repository.PasswordEntityRepository;
import com.authentication.auth.repository.UserEntityRepository;
import com.authentication.auth.service.UserService;
import com.authentication.auth.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImplement implements UserService {

    protected UserEntityRepository userEntityRepository;
    protected PasswordEntityRepository passwordEntityRepository;
    protected PasswordUtils passwordUtils;
    protected JwtServiceImplement jwtService;

    public UserServiceImplement(
            UserEntityRepository userEntityRepository ,
            PasswordUtils passwordUtils,
            PasswordEntityRepository passwordEntityRepository,
            JwtServiceImplement jwtService
    ) {
        this.userEntityRepository = userEntityRepository;
        this.passwordUtils = passwordUtils;
        this.passwordEntityRepository = passwordEntityRepository;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public UserEntity createUser(CreateUserDTO payload) {
        Optional<UserEntity> user = this.userEntityRepository.findByEmail(payload.getEmail());

        if (user.isPresent()) {
            throw new BusinessException(BusinessCode.USER_ALREADY_EXITS);
        }

        PasswordEntity passwordEntity = new PasswordEntity();
        passwordEntity.setHashedPassword(passwordUtils.encryptPassword(payload.getUnHashedPassword()));

        PasswordEntity passOnCommit = this.passwordEntityRepository.save(passwordEntity);

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(payload.getEmail());
        userEntity.setPasswordEntity(passOnCommit);
        userEntity.setCreatedAt(new Date());
        userEntity.setLastName(payload.getLastName());
        userEntity.setFirstName(payload.getFirstName());
        userEntity.setStatus(true);
        userEntity.setVerified(false);
        userEntity.setRequiredOtp(false);
        userEntity.setThirdPartyLogin(true);
        userEntity.setDateOfBirth(payload.getDateOfBirth());

        UserEntity userOnCommit = this.userEntityRepository.save(userEntity);

        TokenDto tokenDto = jwtService.generateToken(userOnCommit);
        jwtService.addTokenCookies(tokenDto);

        return userOnCommit;
    }

    @Override
    public UserEntity updateUser() {
        return null;
    }

    @Override
    public UserEntity deleteUser() {
        return null;
    }

    @Override
    public UserEntity getUser() {
        return null;
    }

    @Override
    public UserEntity getUserById(String id) {
        return null;
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return null;
    }

    @Override
    public UserEntity getUsers() {
        return null;
    }

    @Override
    public String loginCredentials(LoginCredentialDto payload) {
        UserEntity user = this.userEntityRepository.findByEmail(payload.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessCode.USERNAME_NOT_FOUND));

        if (!user.isStatus()) {
            throw new BusinessException(BusinessCode.USER_INACTIVE);
        }

        PasswordEntity passwordEntity = user.getPasswordEntity();

        boolean isMatchesPassword = passwordUtils.comparePasswords(payload.getPassword(), passwordEntity.getHashedPassword());

        if (isMatchesPassword) {
            TokenDto tokenDto = jwtService.generateToken(user);
            jwtService.addTokenCookies(tokenDto);

            return "Login successfully";
        }else {
            throw new BusinessException(BusinessCode.INVALID_CREDENTIALS);
        }

    }

    @Override
    public void logout() {

    }
}
