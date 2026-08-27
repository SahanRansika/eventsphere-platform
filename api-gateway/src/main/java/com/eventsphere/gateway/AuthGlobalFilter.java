package com.eventsphere.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwt;

    public AuthGlobalFilter(JwtUtil jwt) {
        this.jwt = jwt;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Authentication endpoints are public.
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            return chain.filter(exchange);
        }

        // Only the public event catalogue and a single event detail are public.
        // /api/events/admin/all must never be treated as public.
        boolean publicEventRead = method == HttpMethod.GET
                && (path.equals("/api/events")
                || (path.matches("/api/events/\\d+") && !path.equals("/api/events/admin/all")));

        if (publicEventRead) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authorization.substring(7).trim();
        if (token.isBlank() || !jwt.valid(token)) {
            return unauthorized(exchange);
        }

        var claims = jwt.claims(token);
        String userId = String.valueOf(claims.get("userId"));
        String role = String.valueOf(claims.get("role"));
        String fullName = String.valueOf(claims.get("fullName"));
        String userEmail = claims.getSubject();

        boolean adminOnly =
                (path.equals("/api/events/admin/all") && method == HttpMethod.GET)
                        || (path.startsWith("/api/events")
                        && (method == HttpMethod.POST
                        || method == HttpMethod.PUT
                        || method == HttpMethod.DELETE))
                        || path.startsWith("/api/users");

        if (adminOnly && !"ADMIN".equalsIgnoreCase(role)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // Do not trust client-supplied identity headers. Replace them with values
        // extracted from the validated JWT.
        var request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-User-Role");
                    headers.remove("X-User-Name");
                    headers.remove("X-User-Email");
                    headers.set("X-User-Id", userId);
                    headers.set("X-User-Role", role);
                    headers.set("X-User-Name", fullName);
                    headers.set("X-User-Email", userEmail == null ? "" : userEmail);
                })
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
