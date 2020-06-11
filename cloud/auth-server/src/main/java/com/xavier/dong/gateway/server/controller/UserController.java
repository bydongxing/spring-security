package com.xavier.dong.gateway.server.controller;


import com.xavier.dong.gateway.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xavierdong
 * @since 2020-06-05
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor(onConstructor_={@Autowired} )
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_TEST')")
    @GetMapping( value="/normal/test")
    public String test1() {
        return "ROLE_NORMAL /normal/test接口调用成功！";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PreAuthorize("isAuthenticated()")
    @GetMapping( value = "/admin/test")
    public String test2() {
        return "ROLE_ADMIN /admin/test接口调用成功！";
    }

}
