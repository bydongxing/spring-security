package com.xavier.dong.gateway.server.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.xavier.dong.gateway.server.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

//@Component
//@Aspect
@Slf4j
public class AuthTokenAspect {

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        Object proceed = null;
        try {
            proceed = pjp.proceed();
        } catch (Throwable throwable) {
            log.error("执行登录操作失败! 异常信息: {}", throwable.getMessage());
        }
        if (ObjectUtil.isNull(proceed))
            return ResponseEntity.status(HttpStatus.OK).body(Result.createByErrorCodeMessage(HttpStatus.UNAUTHORIZED.value(), "登录失败!"));
        ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>) proceed;
        OAuth2AccessToken body = responseEntity.getBody();
        if (responseEntity.getStatusCode().is2xxSuccessful())
            return ResponseEntity.status(HttpStatus.OK).body(Result.createBySuccess(body));
        log.error("error:{}", responseEntity.getStatusCode().toString());
        return ResponseEntity.status(HttpStatus.OK).body(Result.createByErrorCodeMessage(Result.ResponseCode.BAD_CREDENTIALS.getCode(), "oauth_get_token_fail_user"));

    }
}