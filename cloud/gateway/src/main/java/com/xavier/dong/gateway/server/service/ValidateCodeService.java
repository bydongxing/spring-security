package com.xavier.dong.gateway.server.service;


import com.xavier.dong.gateway.server.common.Result;
import com.xavier.dong.gateway.server.exception.CaptchaException;


/**
 * 验证码处理
 * 
 * @author ruoyi
 */
public interface ValidateCodeService
{
    /**
     * 生成验证码
     */
    public Result createCapcha() throws CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCapcha(String key, String value) throws CaptchaException;
}
