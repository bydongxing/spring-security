package com.xavier.dong.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author XavierDong
 **/
@ConfigurationProperties(prefix = "jwt")
@Configuration
@Data
public class JwtConfig {
    private String security;
    private Long expireTime;
    private String prefix;
    private String header;
}
