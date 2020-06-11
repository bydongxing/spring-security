package com.xavier.dong.common;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一的API返回接口
 *
 * @author xavierdong
 **/
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -8422475523486655584L;
    private Integer code;

    private String msg;

    private T data;

    private String date;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Result() {
    }

    /**
     * 使用了类上面那个注解，在这种情况下，只返回code，不会有msg和data
     *
     * @param code
     */

    public Result(Integer code, String date) {
        this.code = code;
        this.date = date;

    }

    public Result(Integer code, String msg, String date) {
        this.code = code;
        this.msg = msg;
        this.date = date;
    }

    public Result(Integer code, T data, String date) {
        this.code = code;
        this.date = date;
        this.data = data;
    }

    public Result(Integer code, String msg, T data, String date) {
        this.code = code;
        this.date = date;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 在json序列化时，该字段不会显示在json里面 使之不在json序列化结果当中
     *
     * @return
     */
    @JSONField
    public boolean isSuccess() {
        return this.code == HttpStatus.HTTP_OK;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public String getDate() {
        return date;
    }

    /**
     * 成功，返回一个code
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> createBySuccess() {
        return new Result<T>(HttpStatus.HTTP_OK, formatter.format(LocalDateTime.now()));
    }

    /**
     * 成功，返回一个文本供前端提示使用
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> Result<T> createBySuccessMessage(String msg) {
        return new Result<T>(HttpStatus.HTTP_OK, msg, formatter.format(LocalDateTime.now()));
    }

    /**
     * 成功，返回对应的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> createBySuccess(T data) {
        return new Result<T>(HttpStatus.HTTP_OK, data, formatter.format(LocalDateTime.now()));
    }

    /**
     * 成功，把消息和数据都返回
     *
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> createBySuccess(String msg, T data) {
        return new Result<T>(HttpStatus.HTTP_OK, msg, data, formatter.format(LocalDateTime.now()));
    }

    /**
     * 失败，返回一个code
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> createByError() {
        return new Result<T>(HttpStatus.HTTP_BAD_REQUEST, org.springframework.http.HttpStatus.BAD_REQUEST.getReasonPhrase(), formatter.format(LocalDateTime.now()));
    }

    /**
     * 失败，返回一个文本供前端提示使用
     *
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> Result<T> createByErrorMessage(String errorMessage) {
        return new Result<T>(HttpStatus.HTTP_BAD_REQUEST, errorMessage, formatter.format(LocalDateTime.now()));
    }

    /**
     * 失败，返回对应的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> createByError(int erroeCode, T data) {
        return new Result<T>(erroeCode, data, formatter.format(LocalDateTime.now()));
    }

    /**
     * 失败，返回一个status和提示
     *
     * @param erroeCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> Result<T> createByErrorCodeMessage(int erroeCode, String errorMessage) {
        return new Result<T>(erroeCode, errorMessage, formatter.format(LocalDateTime.now()));
    }

    public enum ResponseCode {
        USER_EXISTS(10000, "User Exists"),
        BAD_CREDENTIALS(10001, "BAD CREDENTIALS"),
        USER_DISABLED(10002, "USER DISABLED"),
        USER_LOCKED(10003, "USER LOCKED");

        private final int code;
        private final String desc;


        ResponseCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

    }
}
