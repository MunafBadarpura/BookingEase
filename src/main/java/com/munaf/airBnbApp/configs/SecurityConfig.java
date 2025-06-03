package com.munaf.airBnbApp.configs;

import com.munaf.airBnbApp.entities.enums.Role;
import com.munaf.airBnbApp.filters.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(JwtFilter jwtFilter, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtFilter = jwtFilter;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/admin/**").hasRole(Role.HOTEL_MANAGER.name());
                    auth.requestMatchers("/bookings/**").authenticated();
                    auth.requestMatchers("/auth/signup", "auth/login").anonymous(); //Allow not logged-in  users
                    auth.anyRequest().permitAll();
                })
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionConfig -> exceptionConfig.accessDeniedHandler(accessDeniedHandler()))
                .build();
    }


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
        };
    }
}
