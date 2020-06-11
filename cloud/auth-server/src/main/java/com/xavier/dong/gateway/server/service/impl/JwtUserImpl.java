package com.xavier.dong.gateway.server.service.impl;

import com.xavier.dong.gateway.server.dao.UserMapper;
import com.xavier.dong.gateway.server.entity.dto.JwtUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author XavierDong
 **/
@Service("userDetailsService")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class JwtUserImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        JwtUser jwtUserByUserName = this.userMapper.findJwtUserByUserName(username);
        if (Objects.isNull(jwtUserByUserName))
            throw new UsernameNotFoundException("用户: [" + username + "] 不存在");
        return jwtUserByUserName;
    }
}
