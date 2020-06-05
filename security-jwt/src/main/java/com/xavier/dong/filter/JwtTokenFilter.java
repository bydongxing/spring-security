package com.xavier.dong.filter;

import cn.hutool.core.util.StrUtil;
import com.xavier.dong.common.Const;
import com.xavier.dong.utils.JwtTokenUtil;
import com.xavier.dong.utils.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xavierdong
 */
@Component
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtTokenUtil jwtTokenUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader(Const.HEADER_STRING);
        if (StrUtil.isNotBlank(authHeader) && authHeader.startsWith(Const.TOKEN_PREFIX)) {
            final String authToken = authHeader.substring(Const.TOKEN_PREFIX.length());
            String username = jwtTokenUtil.getUsernameFromToken(authToken);

            log.info("checking authentication for user:  " + username + " who is requesting..." +
                    String.format("%s %s from IP: %s", request.getMethod(), request.getRequestURI(), request.getRemoteAddr()));

            if (StrUtil.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.redisService.getMapField(Const.HEADER_STRING, username);

                if (userDetails == null)
                    userDetails = this.userDetailsService.loadUserByUsername(username);

                if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(authToken, userDetails))) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else
                logger.error("Anoymouse user who is requesting..." + String.format("%s %s from IP: %s", request.getMethod(), request.getRequestURI(), request.getRemoteAddr()));
        }
        chain.doFilter(request, response);
    }
}