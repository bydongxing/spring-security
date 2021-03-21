package com.xavier.dong.handler;//package com.yto.yidam.portal.handler;
//
//import com.alibaba.fastjson.JSON;
//import com.yto.yidam.common.redis.redission.service.RedisService;
//import com.yto.yidam.core.constant.CacheConstants;
//import com.yto.yidam.core.entity.Result;
//import com.yto.yidam.portal.config.JwtConfig;
//import com.yto.yidam.portal.entity.dto.JwtUser;
//import com.yto.yidam.portal.utils.JwtTokenUtil;
//import com.yto.yidam.portal.utils.UserThreadLocalUtil;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
///**
// * 登录成功
// *
// * @author xavierdong
// */
////@Component
////@RequiredArgsConstructor(onConstructor_ = {@Autowired})
//public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private  JwtTokenUtil jwtTokenUtil;
//    private  RedisService redisService;
//    private  JwtConfig jwtConfig;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
//        final String token = this.jwtTokenUtil.generateToken(userDetails);
//        UserThreadLocalUtil.setUserName(userDetails.getEmail());
//        this.redisService.set(CacheConstants.PORTAL_TOKEN.concat(userDetails.getUsername()), userDetails, this.jwtConfig.getExpireTime() * 1000);
//        this.redisService.remove(CacheConstants.PORTAL_LOGIN_FAILED + userDetails.getEmail());
//        httpServletResponse.getWriter().write(JSON.toJSONString(Result.createBySuccess(token)));
//    }
//}