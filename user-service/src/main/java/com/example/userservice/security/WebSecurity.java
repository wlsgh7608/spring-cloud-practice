package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;


@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private Environment env;
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment env,UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    // 권한 부분
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
//                .access("hasIpAddress('" +  "192.168.56.1" + "')") // TODO
                .access("hasIpAddress('" +  "192.168.126.1" + "')") //notebook
//                .access("hasIpAddress('" +  "127.0.0.1" + "')")
                .and()
                .addFilter(getAuthenticationFilter()); // IP address


        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        // 인증 처리
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
        return authenticationFilter;
    }


    // select pwd from users where email=?
    // db_pwd(encrypted) == input_pwd(encrypted)

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

    }
}
