package com.xavier.dong.gateway.server.exception;

/**
 * @author XavierDong
 **/
public class CustomOauthException extends RuntimeException{
    private static final long serialVersionUID = -4457169292707133851L;

    public CustomOauthException(String message) {
        super(message);
    }
}
