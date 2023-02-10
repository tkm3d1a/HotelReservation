package com.reservation.hotel.HotelReservation.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@Configuration
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        authorities.forEach(authority -> {
            if(authority.getAuthority().equals("ROLE_GUEST")) {
                try {
                    //TODO : Figure out how to pass the user model that is pulled for auth?
                    redirectStrategy.sendRedirect(request, response, "/guestprofile");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(authority.getAuthority().equals("ROLE_CLERK")) {
                try {
                    redirectStrategy.sendRedirect(request, response, "/clerkprofile");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(authority.getAuthority().equals("ROLE_ADMIN")) {
                try {
                    redirectStrategy.sendRedirect(request, response, "/adminprofile");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                throw new IllegalStateException();
            }
        });
    }
}
