package com.xavier.dong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xavier.dong.common.Const;
import com.xavier.dong.config.JwtConfig;
import com.xavier.dong.config.LoginFailedConfig;
import com.xavier.dong.dao.UserMapper;
import com.xavier.dong.entity.po.User;
import com.xavier.dong.service.AuthService;
import com.xavier.dong.utils.JwtTokenUtil;
import com.xavier.dong.utils.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xavierdong
 */
@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    private final JwtConfig jwtConfig;

    private final LoginFailedConfig loginFailedConfig;


    @Override
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(upToken);
        } catch (AuthenticationException e) {
            double incr = this.redisService.incrementValue("failed:" + username);
            this.redisService.setExpireTime("failed:" + username, loginFailedConfig.getExpiredTime());
            if (incr > loginFailedConfig.getNum()) {
                log.error("[{}] account failed to login for [{}] consecutive times, and the account was disabled for [{}] minutes. ", username, loginFailedConfig.getNum(), TimeUnit.SECONDS.toMinutes(loginFailedConfig.getExpiredTime()));
                throw new DisabledException("the account [" + username + "] was disabled.");
            }
            throw new BadCredentialsException("The user name or password you entered is not correct. In order to ensure the security of your account, the system will lock your account for [" + TimeUnit.SECONDS.toMinutes(loginFailedConfig.getNum()) + "] minutes if you input the wrong user name or password for [" + TimeUnit.SECONDS.toMinutes(loginFailedConfig.getExpiredTime()) + "]  consecutive times");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        final String token = this.jwtTokenUtil.generateToken(userDetails);
        this.redisService.addMap(Const.HEADER_STRING + userDetails.getUsername(), userDetails.getUsername(), userDetails, this.jwtConfig.getExpireTime() * 1000);
        return token;
    }

    @Override
    public int register(User userToAdd) {
        User user = this.userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, userToAdd.getUsername()));
        if (!Objects.isNull(user))
            return 0;
        userToAdd.setPassword(this.passwordEncoder.encode(userToAdd.getPassword()));
        return this.userMapper.insert(userToAdd);
    }
}