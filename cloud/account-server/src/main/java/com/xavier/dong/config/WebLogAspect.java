package com.xavier.dong.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author xavierdong
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(* com.xavier.dong.controller.*.*(..))")
    public void webLog() {

    }

    @Before("webLog()")
    public void doBefore(JoinPoint point) {
        this.startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("<<<<<<<<<<<<<<<<<<");
        log.info("IP : " + request.getRemoteAddr());
        log.info("URL:" + request.getRequestURL().toString());
        log.info("HTTP_METHOD:" + request.getMethod());
        log.info("CLASS_NAME : " + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
        log.info("ARGS : " + Arrays.toString(point.getArgs()));
    }


    @AfterReturning(pointcut = "webLog()", returning = "ret")
    public void doAferReturning(Object ret) {
        log.info("RESPONSE: " + JSON.toJSONString(ret));
        log.info("Spend Time : " + (System.currentTimeMillis() - this.startTime.get()) + " ms");
        startTime.remove();
        log.info(">>>>>>>>>>>>>>>>>>>>>>>");
    }
}