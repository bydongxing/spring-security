package com.xavier.dong.resource.server.controller;


import com.xavier.dong.resource.server.common.Result;
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
@RequestMapping("/api/role")
public class RoleController {

    @GetMapping("/test")
    public Result testResource() {
        return Result.createBySuccessMessage("你好!");
    }

}
