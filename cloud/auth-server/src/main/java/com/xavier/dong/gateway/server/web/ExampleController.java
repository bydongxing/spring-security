package com.xavier.dong.gateway.server.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xavierdong
 */
@RestController
@RequestMapping("/api/example")
public class ExampleController {

    @RequestMapping("/hello")
    public String hello() {
        return "world";
    }

}