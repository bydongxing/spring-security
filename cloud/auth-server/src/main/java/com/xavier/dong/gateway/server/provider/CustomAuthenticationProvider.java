package com.xavier.dong.gateway.server.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.stereotype.Component;


/**
 * 密码模式获取token失败——参数中userName和password不正确
 *
 * @author xavierdong
 */
@Component
@Slf4j
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //
    }

    /**
     * 验证用户
     * <p>
     * 重写retrieveUser这个方法，这个方法内调用自己的账户服务来认证用户信息，如果用户名密码不匹配时，抛出 InvalidGrantException异常，可以附带message，该异常是AuthenticationException的子类
     * <p>
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        // 伪代码，如果认证失败抛出 InvalidGrantException 然后下面的定义异常解析器oauth2ResponseExceptionTranslator捕获处理
        throw new InvalidGrantException("用户名或密码验证失败");

    }
}
