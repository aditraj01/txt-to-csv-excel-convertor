package com.txt.convertor.config;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    // Bucket storage per IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /* ---------------- BUCKET CONFIG ---------------- */
    private Bucket createNewBucket() {

        Bandwidth limit = Bandwidth.builder()
                .capacity(5) // max 5 requests
                .refillGreedy(5, Duration.ofMinutes(1)) // refill gradually in 1 min
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }

    /* ---------------- FILTER ---------------- */
    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Apply only to convert endpoint
        String path = httpRequest.getRequestURI();
        if (!path.endsWith("/convert")) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIP(httpRequest);
        Bucket bucket = resolveBucket(clientIp);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // Add headers so frontend can show remaining quota
        httpResponse.setHeader("X-Rate-Limit-Remaining",
                String.valueOf(probe.getRemainingTokens()));

        if (probe.isConsumed()) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("""
                    {
                      "error": "Too many requests",
                      "message": "You can upload only 5 files per minute. Please wait."
                    }
                    """);
        }
    }

    /* ---------------- REAL CLIENT IP ---------------- */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}