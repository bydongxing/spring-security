package com.xavier.dong.gateway.server.config;

import com.xavier.dong.gateway.server.entity.dto.JwtUser;
import com.xavier.dong.gateway.server.utils.RedisService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author XavierDong
 **/
public class RedisJwtTokenStore extends JwtTokenStore {

    private final RedisService redisService;

    public RedisJwtTokenStore(RedisService redisService,JwtAccessTokenConverter jwtTokenEnhancer) {
        super(jwtTokenEnhancer);
        this.redisService = redisService;

    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        JwtUser userDetails = (JwtUser) authentication.getUserAuthentication().getPrincipal();
        String value = token.getValue();
        this.redisService.addMap("yidam_token",userDetails.getUsername(),value,36000000);
    }
}
