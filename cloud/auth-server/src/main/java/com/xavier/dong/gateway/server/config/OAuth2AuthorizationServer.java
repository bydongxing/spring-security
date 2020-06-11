package com.xavier.dong.gateway.server.config;

import com.google.common.collect.Lists;
import com.xavier.dong.gateway.server.common.Constant;
import com.xavier.dong.gateway.server.entity.dto.JwtUser;
import com.xavier.dong.gateway.server.exception.CustomWebResponseExceptionTranslator;
import com.xavier.dong.gateway.server.mobile.MobileTokenGranter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;
import java.util.*;

/**
 * 授权服务器配置
 *
 * @author xavierdong
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtConfig jwtConfig;


    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 定义授权和令牌端点以及令牌服务
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                // 请求方式
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                // oauth_approvals
                .approvalStore(approvalStore())
                // 指定token存储位置
                .tokenStore(tokenStore())
                // 授权码模式 持久化 code
                // oauth_code
                .authorizationCodeServices(authorizationCodeServices())
                // 自定义生成令牌
                .tokenEnhancer(tokenEnhancerChain())
                // 用户账号密码认证
                .userDetailsService(userDetailsService)
                // 指定认证管理器
                .authenticationManager(authenticationManager)
                // 是否重复使用 refresh_token
                .reuseRefreshTokens(false)
                //自定义手机验证
                .tokenGranter(tokenGranter(endpoints));
        // 自定义异常处理
        //.exceptionTranslator(new CustomWebResponseExceptionTranslator());
    }



    /**
     * 自定义OAuth2异常处理
     *
     * @return CustomWebResponseExceptionTranslator
     */
    @Bean
    public WebResponseExceptionTranslator<OAuth2Exception> customExceptionTranslator() {
        return new CustomWebResponseExceptionTranslator();
    }


    /**
     * 配置令牌端点(Token Endpoint)的安全约束
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.allowFormAuthenticationForClients().tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }


    /**
     * 授权码模式持久化授权码code
     *
     * @return JdbcAuthorizationCodeServices
     */
    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        // 授权码存储等处理方式类，使用jdbc，操作oauth_code表
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    /**
     * 授权信息持久化实现
     *
     * @return JdbcApprovalStore
     */
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    /**
     * 1、基于 Redis 的token 令牌保存到缓存
     * <p>
     * RedisTokenStore tokenStore = new RedisTokenStore(lettuceConnectionFactory);
     * tokenStore.setPrefix(Constant.OAUTH_ACCESS);
     * return tokenStore;
     * <p>
     * 2、 基于 数据的存储 token ----------> oauth_access_token & oauth_refresh_token
     * <p>
     * return new JdbcTokenStore(dataSource()); 
     * <p>
     * 3、基于 jwt token ，服务端不存储
     * <p>
     * return new JwtTokenStore(accessTokenConverter());
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }


    /**
     * jwt token的生成配置
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(this.jwtConfig.getSecurity());
        return converter;
    }


    /**
     * 配置客户端详情
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // oauth_client_details
        clients.withClientDetails(clientDetailsService());
    }

    /**
     * 声明 ClientDetails实现
     */
    public RedisClientDetailsService clientDetailsService() {
        return new RedisClientDetailsService(dataSource,passwordEncoder);
    }


    /**
     * 自定义token
     *
     * @return tokenEnhancerChain
     */
    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new TokenEnhancer() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                if (accessToken instanceof DefaultOAuth2AccessToken) {
                    DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                    Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();
                    if (!Objects.isNull(authentication.getUserAuthentication())) {
                        JwtUser user = (JwtUser) authentication.getUserAuthentication().getPrincipal();
                        additionalInformation.put(Constant.DETAILS_USER_ID, user.getId());
                    }
                    additionalInformation.put(Constant.DETAILS_USERNAME, authentication.getName());
                    token.setAdditionalInformation(additionalInformation);
                }
                return accessToken;
            }
        }, accessTokenConverter()));
        return tokenEnhancerChain;
    }


    /**
     * 配置自定义的granter,手机号验证码登陆
     *
     * @param endpoints
     * @return
     * @auth joe_chen
     */
    public TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> granters = Lists.newArrayList(endpoints.getTokenGranter());
        granters.add(new MobileTokenGranter(
                authenticationManager,
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));
        return new CompositeTokenGranter(granters);
    }
}

