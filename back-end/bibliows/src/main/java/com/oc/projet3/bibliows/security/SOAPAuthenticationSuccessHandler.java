package com.oc.projet3.bibliows.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * With this code we only want the server to respond with a 200 HTTP status that the login was successful
 *
 */
@Component
public class SOAPAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    /**
     * We make sure we clean up any authentication attributes left in the HttpServletRequest instance, and by default
     * the method will trigger a 200 status instead of a 301 normally sent by a Spring Security form login.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        clearAuthenticationAttributes(request);
    }
}
