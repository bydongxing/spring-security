package com.xavier.dong.gateway.server.exception;


import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.xavier.dong.gateway.server.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 全局异常信息
 *
 * @author xavierdong
 **/
@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    /**
     * ORB 和 stub 之间的应用程序级异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = ApplicationException.class)
    @ResponseBody
    public Result applicationException(ApplicationException e) {
        log.error("Application Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
    }


    /**
     * 参数效验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = WebExchangeBindException.class)
    @ResponseBody
    public Result methodArgumentNotValidException(WebExchangeBindException e) {
        log.error("Argument not valid: [{}]", e.getMessage());
        log.error(JSON.toJSONString(e.getBindingResult().getAllErrors()));
        return Result.createByError(HttpStatus.HTTP_BAD_REQUEST, e.getFieldErrors().stream().collect(HashMap::new, (m, v) -> m.put(v.getField(), v.getDefaultMessage()), HashMap::putAll));
    }


    /**
     * 参数效验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Result methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Argument not valid: [{}]", e.getMessage());
        log.error(JSON.toJSONString(e.getBindingResult().getAllErrors()));
        return Result.createByError(HttpStatus.HTTP_BAD_REQUEST, e.getBindingResult().getFieldErrors().stream().collect(HashMap::new, (m, v) -> m.put(v.getField(), v.getDefaultMessage()), HashMap::putAll));
    }


    /**
     * 参数非法异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = IllegalStateException.class)
    @ResponseBody
    public Result illegalStateException(IllegalStateException e) {
        log.error("Argument not valid(Illegal): [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());

    }


    /**
     * 接口不存在异常
     *
     * @param request 请求的request
     * @return 响应体
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    public Result noHandlerFoundExceptionHandler(HttpServletRequest request, NoHandlerFoundException e) {
        log.error("Url not found Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_NOT_FOUND, "接口 [" + request.getRequestURI() + "] 不存在");
    }

    /**
     * Precondition failed异常
     *
     * @return 响应体
     */
    @ExceptionHandler(value = ServletException.class)
    @ResponseBody
    public Result servletExceptionHandler(ServletException e) {
        log.error("Servlet Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_NOT_ACCEPTABLE, e.getMessage());
    }

    /**
     * ValidationException
     *
     * @return 响应体
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public Result constraintViolationExceptionHandler(ValidationException e) {
        log.error("Validation Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
    }

    /**
     * 实现对异常的拦截，当错误在Bean的注解时
     *
     * @return 响应体
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Result bindExceptionHandler(BindException e) {
        log.error("Bind Exception: [{}]", e.getMessage());
        log.error(JSON.toJSONString(e.getBindingResult().getAllErrors()));
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_BAD_REQUEST, e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(",")));
    }


    /**
     * 用户名或者密码错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseBody
    public Result badCredentialsException(BadCredentialsException e) {
        log.error("Bad Credentials Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(Result.ResponseCode.BAD_CREDENTIALS.getCode(), e.getMessage());
    }

    /**
     * 用户被禁用
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = DisabledException.class)
    @ResponseBody
    public Result disabledException(DisabledException e) {
        log.error("Disabled Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(Result.ResponseCode.USER_DISABLED.getCode(), e.getMessage());
    }


    /**
     * 用户被锁定
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = LockedException.class)
    @ResponseBody
    public Result lockedException(LockedException e) {
        log.error("Locked Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(Result.ResponseCode.USER_LOCKED.getCode(), e.getMessage());
    }

    /**
     *
     * 无访问权限
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result handleAccessDeniedException(AccessDeniedException e){
        log.error("AccessDenied Exception: [{}]", e.getMessage());
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_FORBIDDEN, e.getMessage());

    }

    /**
     * Precondition failed异常
     *
     * @param req 请求体
     * @param e   异常
     * @return 响应体
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result defaultExceptionHandler(HttpServletRequest req, Exception e, Object handler) {
        String message;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            message = String.format("接口 [%s] 出现异常，方法：%s.%s，异常摘要：%s",
                    req.getRequestURI(),
                    handlerMethod.getBean().getClass().getName(),
                    handlerMethod.getMethod().getName(),
                    e.getMessage());
        } else {
            message = e.getMessage();
        }
        log.error(message, e);
        return Result.createByErrorCodeMessage(HttpStatus.HTTP_INTERNAL_ERROR, "接口 [" + req.getRequestURI() + "] 内部错误");
    }

}
