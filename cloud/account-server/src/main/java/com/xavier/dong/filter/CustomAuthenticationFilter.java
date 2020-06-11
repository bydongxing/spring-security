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
}