package com.reservation.hotel.HotelReservation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                //TODO: Figure out why the role assigned does not work?
                //TODO: update so that only admins can access admin page/guests guest pages/clerks clerk pages
//                .requestMatchers("/guest-profile/**").hasAnyRole("ROLE_GUEST")
//                .requestMatchers("/clerk-profile/**").hasAnyRole("ROLE_CLERK")
                .requestMatchers("/", "/login", "/register/**", "/result", "/rooms/**", "/setup/addAdmin", "/images/**").permitAll()
                .anyRequest()
                .authenticated());


        http.formLogin(form -> form.loginPage("/login").successHandler(loginSuccessHandler));
        http.httpBasic();
        http.csrf().disable();
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
