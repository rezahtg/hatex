package com.reza.hatex.utils;

import com.reza.hatex.helper.UserAttempts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final static long MAX_ATTEMPTS = 5;
    private final static long TIME_FRAME = 60000;
    private ConcurrentHashMap<String, UserAttempts> attemptCache = new ConcurrentHashMap<>();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        UserAttempts userAttempts = attemptCache.getOrDefault(ip, new UserAttempts(0, System.currentTimeMillis()));

        if (System.currentTimeMillis() - userAttempts.getTimestamp() > TIME_FRAME) {
            userAttempts.resetAttempts();
        }

        if (userAttempts.getAttempts() >= MAX_ATTEMPTS) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Too many registration attempts. Try again later.");
            return;
        }

        attemptCache.put(ip, userAttempts);
        filterChain.doFilter(request, response);

    }
}
