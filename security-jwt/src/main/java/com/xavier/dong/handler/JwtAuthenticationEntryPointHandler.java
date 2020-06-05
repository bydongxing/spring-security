package com.xavier.dong.handler;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.xavier.dong.common.Result;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败
 * <p>
 * 自定401返回值
 *
 * @author xavierdong
 */
@Component
public class JwtAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Result result = null;
        /**身份认证未通过*/
        if (e instanceof BadCredentialsException) {
            result = Result.createByErrorCodeMessage(HttpStatus.HTTP_UNAUTHORIZED, "用户名或密码错误，请重新输入");
        } else {
            result = Result.createByErrorCodeMessage(HttpStatus.HTTP_BAD_REQUEST, "无效的token！");
        }
        response.getWriter().write(JSON.toJSONString(result));
    }

}