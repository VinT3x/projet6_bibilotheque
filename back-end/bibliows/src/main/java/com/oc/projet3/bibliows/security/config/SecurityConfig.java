package com.oc.projet3.bibliows.security.config;

import com.oc.projet3.bibliows.security.BiblioUserDetailsService;
import com.oc.projet3.bibliows.security.SOAPAuthenticationEntryPoint;
import com.oc.projet3.bibliows.security.SOAPAuthenticationFailureHandler;
import com.oc.projet3.bibliows.security.SOAPAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
//@EnableWebSecurity(debug = false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BiblioUserDetailsService userDetailsService;

    /**
     * We need a custom authenticationEntryPoint because default Spring-Security config will
     * redirect to login page
     */
    @Autowired
    private SOAPAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * We need a custom AuthenticationFailureHandler. It will return http status code 401
     */
    @Autowired
    private SOAPAuthenticationFailureHandler authenticationFailureHandler;

    /**
     * We need a custom AuthentificationSuccessHandler. It will return http status 200 with
     * user info in json format.
     */
    @Autowired
    private SOAPAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    DataSource dataSource;


    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
        .httpBasic()
                .and()
                .authorizeRequests().antMatchers("/anonymous/**").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET,"/ws/wsBooks.wsdl").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST,"/ws/**").hasAuthority("USER")
//                .authorizeRequests().antMatchers("/ws/**").permitAll()
//                .authorizeRequests().antMatchers(HttpMethod.POST, "/ws/createAuthor", "/ws/createAuthorRequest", "/ws/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable().headers().frameOptions().disable();
;
        httpSecurity.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        httpSecurity.formLogin().successHandler(authenticationSuccessHandler);
        httpSecurity.formLogin().failureHandler(authenticationFailureHandler);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider
                = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(getPasswordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

