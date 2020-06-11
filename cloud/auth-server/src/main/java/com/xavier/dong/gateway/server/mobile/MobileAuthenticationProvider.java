package com.xavier.dong.gateway.server.mobile;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 手机验证码登陆provider
 */
public class MobileAuthenticationProvider extends DaoAuthenticationProvider {

    public MobileAuthenticationProvider(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}