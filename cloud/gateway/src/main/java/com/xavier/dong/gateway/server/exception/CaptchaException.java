package com.xavier.dong.gateway.server.exception;

/**
 * 验证码错误异常类
 *
 * @author xavierdong
 */
public class CaptchaException extends RuntimeException {


    private static final long serialVersionUID = -2901142607777443595L;

    public CaptchaException(String msg) {
        super(msg);
    }
}
