package com.xavier.dong.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 登录失败的次数重置功能
 *
 * @author <a href="mailto:xing.dong@sha.ontime-express.com">XavierDong</a>
 **/
@Data
@ConfigurationProperties(prefix = "portal.login")
@Configuration
//@RefreshScope
public class LoginFailedConfig {

    /**
     * 失效时间(秒)
     */
    private Long expiredTime;

    /**
     * 登录失败的次数
     */
    private Integer num;

}
