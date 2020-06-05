package com.xavier.dong.handler;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.xavier.dong.common.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定403返回值
 *
 * @author xavierdong
 */
@Component
public class JwtRestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JSON.toJSONString(Result.createByErrorCodeMessage(HttpStatus.HTTP_FORBIDDEN, "无访问权限!")));
    }

}