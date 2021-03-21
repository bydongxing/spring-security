package com.xavier.dong.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.net.HttpHeaders;
import com.yto.yidam.common.redis.redission.service.RedisService;
import com.yto.yidam.core.constant.CacheConstants;
import com.yto.yidam.core.entity.Result;
import com.yto.yidam.core.exception.CustomException;
import com.yto.yidam.portal.config.JwtConfig;
import com.yto.yidam.portal.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出登录
 *
 * @author xavierdong
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RedisService redisService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtConfig jwtConfig;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isBlank(token))
            throw new CustomException(String.valueOf(Result.ResponseCode.NOT_LOGIN.getCode()));
        String userName = this.jwtTokenUtil.getUsernameFromToken(token.substring(jwtConfig.getPrefix().trim().length()));
        if (StrUtil.isBlank(userName))
            throw new CustomException("10007");
        this.redisService.remove(CacheConstants.PORTAL_TOKEN.concat(userName));
        SecurityContextHolder.clearContext();
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.createBySuccess()));
    }
}