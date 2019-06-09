package com.oc.projet3.bibliows.security;

import com.oc.projet3.bibliows.endpoints.AuthorEndpoint;
import com.oc.projet3.bibliows.service.AuthorService;
import com.oc.projet3.bibliows.service.AuthorServiceImpl;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * We need a custom authenticationEntryPoint because default Spring-Security config will redirect to login page.
 * In our case we need just a http status 401 and a json response.
 *
 */
@Component
public class SOAPAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * This code simply responds with a 401 Unauthorized status code as
     * soon as there’s an authentication problem
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

    }

}