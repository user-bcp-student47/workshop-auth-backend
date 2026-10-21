package com.bcp.workshop.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ApplicationScoped
public class TokenService {
    private static final long TOKEN_TTL_SECONDS = 3600;
    private static final int MINIMUM_SECRET_BYTES = 32;

    private final Algorithm algorithm;
    private final Clock clock;

    @Inject
    TokenService(@ConfigProperty(name = "auth.token.secret") String secret) {
        this(secret, Clock.systemUTC());
    }

    TokenService(String secret, Clock clock) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MINIMUM_SECRET_BYTES) {
            throw new IllegalArgumentException("AUTH_TOKEN_SECRET must contain at least 32 UTF-8 bytes");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.clock = clock;
    }

    public IssuedToken issue(String username, String role, String client) {
        Instant issuedAt = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        Instant expiresAt = issuedAt.plusSeconds(TOKEN_TTL_SECONDS);
        String token = JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withClaim("client", client)
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
        return new IssuedToken(token, Math.toIntExact(expiresAt.getEpochSecond() - issuedAt.getEpochSecond()));
    }

    public record IssuedToken(String value, int expiresIn) {}
}