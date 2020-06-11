package com.xavier.dong.gateway.server.filter;

import com.alibaba.fastjson.JSON;
import com.xavier.dong.gateway.server.common.Result;
import com.xavier.dong.gateway.server.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 验证码过滤器
 *
 * @author xavierdong
 */
@Component
public class ValidateCodeFilter extends AbstractGatewayFilterFactory<Object> {

    private final static String AUTH_URL = "/oauth/token";

    @Autowired
    private ValidateCodeService validateCodeService;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 非登录请求，不处理
//            if (!StringUtils.containsIgnoreCase(request.getURI().getPath(), AUTH_URL)) {
            // todo 解开验证码效验
            if (true) {
                return chain.filter(exchange);
            }
            try {
                validateCodeService.checkCapcha(request.getQueryParams().getFirst("code"),
                        request.getQueryParams().getFirst("uuid"));
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                return exchange.getResponse().writeWith(
                        Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(Result.createByErrorMessage(e.getMessage())))));
            }
            return chain.filter(exchange);
        };
    }
}
