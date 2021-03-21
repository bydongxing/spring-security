package com.xavier.dong.handler;//package com.yto.yidam.portal.handler;
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSON;
//import com.yto.yidam.common.redis.redission.service.RedisService;
//import com.yto.yidam.core.constant.CacheConstants;
//import com.yto.yidam.core.util.ErrorResultUtil;
//import com.yto.yidam.portal.config.PortalLoginConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.concurrent.TimeUnit;
//
///**
// * 登录失败 逻辑
// *
// * @author xavierdong
// */
////@Component
//@Slf4j
////@RequiredArgsConstructor(onConstructor_ = {@Autowired})
//public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
//
//    private ErrorResultUtil errorResultUtil;
//    private RedisService redisService;
//    private PortalLoginConfig loginFailedConfig;
//    private static final String LANGUAGE;
//    private static final String DEFAULT_LANGUAGE;
//
//    static {
//        LANGUAGE = "language";
//        DEFAULT_LANGUAGE = "en";
//    }
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
//        String languageCode = StrUtil.isBlank(httpServletRequest.getHeader(LANGUAGE)) ? DEFAULT_LANGUAGE : httpServletRequest.getHeader(LANGUAGE);
//        PrintWriter printWriter = httpServletResponse.getWriter();
//        if (e instanceof DisabledException) {
//            printWriter.write(JSON.toJSONString(errorResultUtil.createErrorResult("10006", languageCode)));
//            printWriter.flush();
//            printWriter.close();
//        }
//        String username = httpServletRequest.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
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
//}