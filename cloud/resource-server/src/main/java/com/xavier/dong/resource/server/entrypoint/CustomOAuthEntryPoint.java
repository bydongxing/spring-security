package com.xavier.dong.resource.server.entrypoint;

import com.alibaba.fastjson.JSON;
import com.xavier.dong.resource.server.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * resource服务器请求，验证token失败(未带token/token失效)时返回值重写
 *
 * @author xavierdong
 */
@Component
@Slf4j
public class CustomOAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws ServletException {
        Throwable cause = authException.getCause();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            if (cause instanceof OAuth2AccessDeniedException) {
                // 资源权限不足
                response.getWriter().write(JSON.toJSONString(Result.createByError(HttpStatus.FORBIDDEN.value(), "oauth_access_resource_insufficient_authority")));
            } else if (cause == null || cause instanceof InvalidTokenException) {
                // 未带token或token无效
                // cause == null 一般可能是未带token
                response.getWriter().write(JSON.toJSONString(Result.createByError(HttpStatus.FORBIDDEN.value(), "oauth_access_resource_token_invalid")));

            }
        } catch (IOException e) {
            log.error("其他异常error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}