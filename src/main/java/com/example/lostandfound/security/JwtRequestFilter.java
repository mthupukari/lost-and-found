package com.example.lostandfound.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired");
            } catch (Exception e) {
                logger.warn("Unable to extract JWT Token");
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //TODO: load user details
            if (jwtUtil.validateToken(jwtToken, username)) {
                logger.info("JWT Token is valid for user: " + username);
            }
        }

        chain.doFilter(request, response);
    }
    
}
