package com.xavier.dong.gateway.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) {

        OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
//        return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
//                .body(new CustomOauthException(oAuth2Exception));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CustomOauthException(oAuth2Exception));
    }
}