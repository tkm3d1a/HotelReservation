package com.reservation.hotel.HotelReservation.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@Configuration
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        authorities.forEach(authority -> {
            if(authority.getAuthority().equals("ROLE_GUEST")) {
                try {
                    //TODO : Figure out how to pass the user model that is pulled for auth?
                    User user = (User) authentication.getPrincipal();
                    log.info("{}", user);
                    log.info("Guest log in success!");
                    redirectStrategy.sendRedirect(request, response, "/guest-profile");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(authority.getAuthority().equals("ROLE_CLERK")) {
                try {
                    User user = (User) authentication.getPrincipal();
                    log.info("{}", user);
                    log.info("Clerk log in success!");
                    redirectStrategy.sendRedirect(request, response, "/clerk-profile");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(authority.getAuthority().equals("ROLE_ADMIN")) {
                try {
                    User user = (User) authentication.getPrincipal();
                    log.info("{}", user);
                    log.info("Clerk log in success!");
                    redirectStrategy.sendRedirect(request, response, "/admin-profile");
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
