package com.xavier.dong.gateway.server.mobile;


import com.xavier.dong.gateway.server.dao.UserMapper;
import com.xavier.dong.gateway.server.entity.dto.JwtUser;
import com.xavier.dong.gateway.server.service.impl.JwtUserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 手机验证码登陆, 用户相关获取
 */
@Slf4j
@Service("mobileUserDetailsService")
public class MobileUserDetailsService extends JwtUserImpl {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;


    public MobileUserDetailsService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        super(userMapper);
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        JwtUser jwtUserByUserName = this.userMapper.findJwtUserByPhone(username);

        if (Objects.isNull(jwtUserByUserName))
            throw new UsernameNotFoundException("手机号: [" + username + "] 不存在");

        // 如果为mobile模式，从短信服务中获取验证码（动态密码）
        // todo  动态 从 短信服务中 获取 验证码 （动态密码）
//        String credentials = smsCodeProvider.getSmsCode(uniqueId, "LOGIN");
        jwtUserByUserName.setPassword(this.passwordEncoder.encode("123456"));
        return jwtUserByUserName;
    }
}
