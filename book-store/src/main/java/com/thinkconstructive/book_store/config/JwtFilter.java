package com.thinkconstructive.book_store.config;

import com.thinkconstructive.book_store.service.JWTService;
import com.thinkconstructive.book_store.service.impl.UserInfoUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Bearer JWTTOKEN-Value

        String jwtToken = null;
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer"))
        {
            jwtToken = authHeader.substring(7);
            username = jwtService.extractUserName(jwtToken);
        }

        if(username !=null &&
                SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails =
                    applicationContext.getBean(UserInfoUserDetailsServiceImpl.class)
                            .loadUserByUsername(username);

            if(jwtService.validateToken(jwtToken, userDetails))
            {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
    filterChain.doFilter(request, response);
    }
}
