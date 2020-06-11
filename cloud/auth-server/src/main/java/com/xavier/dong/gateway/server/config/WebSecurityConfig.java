package com.xavier.dong.gateway.server.config;


import com.xavier.dong.gateway.server.mobile.MobileAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author xavierdong
 */
@Configuration
@EnableWebSecurity
// 在授权之前必须完成登录，所以WebSecurityConfig必须必须在ResourceServerConfig之前，如果不是的话就会出现如下异常，所以在WebSecurityConfig上加了个注解@Order(1)
// User must be authenticated with Spring Security before authorization can be completed.
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("mobileUserDetailsService")
    private UserDetailsService mobileUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 让Security 忽略这些url，不做拦截处理
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers
                ("/swagger-ui.html/**", "/webjars/**",
                        "/swagger-resources/**", "/v2/api-docs/**",
                        "/swagger-resources/configuration/ui/**", "/swagger-resources/configuration/security/**",
                        "/images/**");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        // 设置手机验证码登陆的AuthenticationProvider
        auth.authenticationProvider(mobileAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.csrf().disable();

//        http.formLogin().and()
//                .requestMatchers()
//                .antMatchers("/login", "/oauth/**", "/actuator/**", "/token/**")
//                .and()
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated();

        http
                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll().and().logout().permitAll();

//        http
//                .formLogin()
//                .loginPage("/portal/login")//用户未登录时，访问任何资源都转跳到该路径，即登录页面
//                .loginProcessingUrl("/login")//登录表单form中action的地址，也就是处理认证请求的路径
//                .defaultSuccessUrl("/portal/index",true)//登录认证成功后默认转跳的路径，第二个参数为true则任何情况都跳到指定url。否则会先跳到referer，referer为空才跳到指定url
//                .usernameParameter("username")///登录表单form中用户名输入框input的name名，不修改的话默认是username
//                .passwordParameter("password")//form中密码输入框input的name名，不修改的话默认是password
//                .and()
//                .authorizeRequests()
//                .antMatchers("/portal/login","/portal/index").permitAll()//不需要通过登录验证就可以被访问的资源路径
//                .anyRequest().authenticated();


    }

    /**
     * 创建手机验证码登陆的AuthenticationProvider
     *
     * @return mobileAuthenticationProvider
     */
    @Bean
    public MobileAuthenticationProvider mobileAuthenticationProvider() {
        MobileAuthenticationProvider mobileAuthenticationProvider = new MobileAuthenticationProvider(this.mobileUserDetailsService);
        mobileAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return mobileAuthenticationProvider;
    }

}