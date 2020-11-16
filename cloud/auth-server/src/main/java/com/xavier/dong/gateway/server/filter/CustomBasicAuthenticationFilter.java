package com.xavier.dong.gateway.server.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.net.HttpHeaders;
import com.xavier.dong.gateway.server.common.Result;
import com.xavier.dong.gateway.server.exception.CustomOauthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * basic auth 方式client认证过滤器
 * 置于{@link org.springframework.security.web.authentication.www.BasicAuthenticationFilter}之前，
 * 以实现客户端信息不全、认证失败时返回自定义响应信息
 * <p>
 * 客户端/密码模式获取token失败——参数中未携带client_id、参数中client_id或client_secret不正确
 * <p>
 * 这里有一个前提是client验证必须是basic auth方式，即在请求头中设置Authorization参数，将client_id和client_secret以:间隔进行拼接，然后将拼接后的字符串使用 BASE64 编码与Basic拼接，可生成 Authorization 参数的值
 * <p>
 *
 * @author xavierdong
 */
@Component
@Slf4j
public class CustomBasicAuthenticationFilter extends OncePerRequestFilter {

    private static final String OAUTH_TOKEN = "/oauth/token";

    private static final String BASIC = "basic";

    @Resource
    private ClientDetailsService clientDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().contains(OAUTH_TOKEN)) {
            filterChain.doFilter(request, response);
            return;
        }

        String[] clientDetails = this.isHasClientDetails(request);
        // 客户端信息缺失
        if (clientDetails == null) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.createByErrorMessage("oauth get token failed. reason: client info is missed.")));
            return;
        }

        try {
            this.handle(request, response, clientDetails, filterChain);
        } catch (CustomOauthException coe) {
            // 客户端认证失败
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.createByErrorMessage("oauth get token failed. reason: " + coe.getMessage())));

        }

    }

    private void handle(HttpServletRequest request, HttpServletResponse response, String[] clientDetails, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        ClientDetails details = null;
        try {
            details = this.getClientDetailsService().loadClientByClientId(clientDetails[0]);
        } catch (ClientRegistrationException e) {
            log.error("client认证失败，{},{}", e.getMessage(), clientDetails[0]);
            throw new CustomOauthException("client_id 或client_secret 不正确");
        }

        if (details == null) {
            log.info("client认证失败，{}", clientDetails[0]);
            throw new CustomOauthException("client_id或client_secret不正确");
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(details.getClientId(), details.getClientSecret(), details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }

    /**
     * 判断请求头中是否包含client信息，不包含返回null  Base64编码
     */
    private String[] isHasClientDetails(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isBlank(header)) {
            String id = request.getParameter("client_id");
            String secret = request.getParameter("client_secret");
            if (StrUtil.isBlank(id))
                return null;
            return new String[]{id, secret};
        }
        String basic = header.substring(0, 5);
        if (basic.toLowerCase().contains(BASIC)) {
            String tmp = header.substring(6);
            String defaultClientDetails = new String(Base64.getDecoder().decode(tmp));
            String[] clientArrays = defaultClientDetails.split(":");

            if (clientArrays.length == 2)
                return clientArrays;
            return null;
        }
        return null;
    }

    public ClientDetailsService getClientDetailsService() {
        return clientDetailsService;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }
}
