package com.xavier.dong.controller;

import com.xavier.dong.common.Result;
import com.xavier.dong.entity.form.AuthenticationBean;
import com.xavier.dong.entity.po.User;
import com.xavier.dong.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author xavierdong
 */
@RestController
@AllArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/authentication")
public class JwtAuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public Result createToken(@RequestBody @Valid AuthenticationBean authenticationBean) {
        return Result.createBySuccess(this.authService.login(authenticationBean.getUsername(), authenticationBean.getPassword()));
    }

    @PostMapping(value = "/register")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result register(@RequestBody User addedUser) {
        return this.authService.register(addedUser) == 0 ? Result.createByErrorCodeMessage(Result.ResponseCode.USER_EXISTS.getCode(), "用户已经存在!") : Result.createBySuccess();
    }

}