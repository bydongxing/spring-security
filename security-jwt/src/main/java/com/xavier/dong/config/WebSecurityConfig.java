package com.xavier.dong.config;

import com.xavier.dong.filter.CustomAuthenticationFilter;
import com.xavier.dong.filter.JwtTokenFilter;
import com.xavier.dong.handler.JwtAuthenticationEntryPointHandler;
import com.xavier.dong.handler.JwtRestAccessDeniedHandler;
import com.xavier.dong.utils.JwtTokenUtil;
import com.xavier.dong.utils.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author xavierdong
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtAuthenticationEntryPointHandler entryPointUnauthorizedHandler;

    @Autowired
    private JwtRestAccessDeniedHandler restAccessDeniedHandler;


    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 用于访问一些静态的东西控制。
     * 其中ignoring()方法可以让访问跳过filter验证
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
//		web.ignoring().mvcMatchers("/api/v1/user/login");
        web.ignoring().antMatchers("/swagger-ui.html/**", "/webjars/**",
                "/swagger-resources/**", "/v2/api-docs/**",
                "/swagger-resources/configuration/ui/**", "/swagger-resources/configuration/security/**",
                "/images/**");
    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.passwordEncoder());
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
//        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(jwtTokenUtil,redisService,jwtConfig,errorResultUtil,portalLoginConfig);
//        filter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
//        filter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setFilterProcessesUrl("/api/v1/portal/authentication/login");
        return filter;
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {



        httpSecurity.csrf().disable()
                .addFilterBefore(this.jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                //主要配置如果没有凭证，可以进行一些操作 (exceptionHandling().authenticationEntryPoint())
                .exceptionHandling().authenticationEntryPoint(entryPointUnauthorizedHandler).accessDeniedHandler(restAccessDeniedHandler)
                .and()
                // 定制我们自己的 session 策略：调整为让 Spring Security 不创建和使用 session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/authentication/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .headers().cacheControl();


//        // 去掉 CSRF
//        httpSecurity.csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用 JWT，关闭token
//                .and()
//                .httpBasic().authenticationEntryPoint(jwtAuthenticationEntryPointHandler)
//                .and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/v1/portal/authentication/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(jwtTokenFilter, CustomAuthenticationFilter.class)
//                .addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .formLogin()  //开启登录
////                .loginProcessingUrl("/api/v1/portal/authentication/login")
////                .successHandler(jwtAuthenticationSuccessHandler)
////                .failureHandler(jwtAuthenticationFailureHandler)
//                .permitAll()
//                .and()
//                .logout()
//                .logoutUrl("/api/v1/portal/authentication/logout")
//                .logoutSuccessHandler(jwtLogoutSuccessHandler)
//                .permitAll().and()
//                .headers().cacheControl();
//
//        // 记住我
////        httpSecurity.rememberMe().rememberMeParameter("remember-me")
////                .userDetailsService(userDetailsService).tokenValiditySeconds(300);
//
//        httpSecurity.exceptionHandling().accessDeniedHandler(jwtRestAccessDeniedHandler); // 无权访问 JSON 格式的数据
////        httpSecurity.addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // 替换 UsernamePasswordAuthenticationFilter
////        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class); // JWT Filter
    }

}