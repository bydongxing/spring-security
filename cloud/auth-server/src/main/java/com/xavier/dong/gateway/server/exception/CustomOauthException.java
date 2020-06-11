package com.xavier.dong.gateway.server.exception;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xavier.dong.gateway.server.common.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
class CustomOauthException extends OAuth2Exception {

    private final Result result;

    CustomOauthException(OAuth2Exception oAuth2Exception) {
        super(oAuth2Exception.getSummary(), oAuth2Exception);

//        INVALID_USERNAME_PASSWORD
//        System.out.println(oAuth2Exception.getSummary());
//        System.out.println(oAuth2Exception.getOAuth2ErrorCode());
//        System.out.println(oAuth2Exception.getHttpErrorCode());
        if (StrUtil.contains(oAuth2Exception.getSummary(), "Bad credentials"))
            this.result = Result.createByError(Result.ResponseCode.BAD_CREDENTIALS.getCode(), oAuth2Exception);
        else
            this.result = Result.createByError(Result.ResponseCode.USER_DISABLED.getCode(), oAuth2Exception);

    }
}