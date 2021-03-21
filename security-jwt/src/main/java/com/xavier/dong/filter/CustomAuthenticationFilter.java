package com.xavier.dong.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavier.dong.entity.form.AuthenticationBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;


@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {

            //use jackson to deserialize json
            ObjectMapper mapper = new ObjectMapper();
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
            try (InputStream is = request.getInputStream()) {
                AuthenticationBean authenticationBean = mapper.readValue(is, AuthenticationBean.class);
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationBean.getUsername(), authenticationBean.getPassword());
            } catch (IOException e) {
                log.error("get username and password error. exception: {}", e.getMessage());
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken("", "");
            }
            super.setDetails(request, usernamePasswordAuthenticationToken);
            return super.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);

        } else
            return super.attemptAuthentication(request, response);
    }



//    @Override
//    protected void successfulAuthentication(HttpServletRequest request,
//                                            HttpServletResponse response,
//                                            FilterChain chain,
//                                            Authentication authentication) throws IOException, ServletException {
//
//        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
//        final String token = this.jwtTokenUtil.generateToken(userDetails);
//        this.redisService.set(CacheConstants.PORTAL_TOKEN.concat(userDetails.getUsername()), userDetails, this.jwtConfig.getExpireTime() * 1000);
//        this.redisService.remove(CacheConstants.PORTAL_LOGIN_FAILED + userDetails.getEmail());
//        response.getWriter().write(JSON.toJSONString(Result.createBySuccess(token)));
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
//        String languageCode = StrUtil.isBlank(httpServletRequest.getHeader(LANGUAGE)) ? DEFAULT_LANGUAGE : httpServletRequest.getHeader(LANGUAGE);
//        PrintWriter printWriter = httpServletResponse.getWriter();
//        if (e instanceof DisabledException) {
//            printWriter.write(JSON.toJSONString(errorResultUtil.createErrorResult("10006", languageCode)));
//            printWriter.flush();
//            printWriter.close();
//        }
////        String username = httpServletRequest.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
//        String username = authenticationBean.getUsername();
//        double incr = this.redisService.incrementValue(CacheConstants.PORTAL_LOGIN_FAILED + username);
//        this.redisService.setExpireTime(CacheConstants.PORTAL_LOGIN_FAILED + username, loginFailedConfig.getLogin().getExpiredTime());
//        if (incr > loginFailedConfig.getLogin().getNum()) {
//            log.error("[{}] account failed to login for [{}] consecutive times, and the account was disabled for [{}] minutes. ", username, loginFailedConfig.getLogin().getNum(), TimeUnit.SECONDS.toMinutes(loginFailedConfig.getLogin().getExpiredTime()));
//            printWriter.write(JSON.toJSONString(errorResultUtil.createErrorResult("10006", languageCode)));
//            printWriter.flush();
//            printWriter.close();
//        }
//        log.error("The user name or password you entered is not correct. In order to ensure the security of your account, the system will lock your account for [" + TimeUnit.SECONDS.toMinutes(loginFailedConfig.getLogin().getExpiredTime()) + "] minutes if you input the wrong user name or password for [" + TimeUnit.SECONDS.toMinutes(loginFailedConfig.getLogin().getExpiredTime())
//                + "]  consecutive times");
//        printWriter.write(JSON.toJSONString(errorResultUtil.createErrorResult("10005", languageCode)));
//        printWriter.flush();
//        printWriter.close();
//    }
}