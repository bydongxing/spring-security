package com.xavier.dong.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 用户状态
 *
 * @author XavierDong
 **/
public enum UserStatus {

    ENABLED(1, "启用"),
    DISBLED(0, "禁用");

    @EnumValue
    private Integer code;
    private String des;


    UserStatus(Integer code, String des) {
        this.code = code;
        this.des = des;
    }

    public Integer getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }

    @Override
    public String toString() {
        return this.des;
    }
}
