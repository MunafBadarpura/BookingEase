package com.munaf.airBnbApp.filters;

import com.munaf.airBnbApp.entities.User;
import com.munaf.airBnbApp.repositories.UserRepository;
import com.munaf.airBnbApp.utils.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtFilter(JwtService jwtService, UserRepository userRepository, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestHeader = request.getHeader("Authorization");
            if (requestHeader == null || !requestHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwtToken = requestHeader.split("Bearer ")[1];
            String tokenType = jwtService.getTokenType(jwtToken);
            if (!"ACCESS".equals(tokenType)) {
                throw new JwtException("Invalid token type: " + tokenType + ". ACCESS token required.");
            }
            Long userId = jwtService.getUserIdFromToken(jwtToken);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User id not found with Id : " + userId));

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
