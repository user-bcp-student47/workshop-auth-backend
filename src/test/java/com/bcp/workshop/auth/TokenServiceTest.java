package com.bcp.workshop.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceTest {
    private static final String TEST_TOKEN_SECRET = "workshop-test-only-signing-key-32-bytes";
    private static final Instant ISSUED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void issuesSignedTokenWithClaimsAndAlignedExpiration() {
        TokenService service = new TokenService(TEST_TOKEN_SECRET, Clock.fixed(ISSUED_AT, ZoneOffset.UTC));

        TokenService.IssuedToken issued = service.issue("student", "STUDENT", "angular-workshop");

        DecodedJWT decoded = JWT.decode(issued.value());
        assertEquals("student", decoded.getSubject());
        assertEquals("STUDENT", decoded.getClaim("role").asString());
        assertEquals("angular-workshop", decoded.getClaim("client").asString());
        assertEquals("HS256", decoded.getAlgorithm());
        assertEquals(ISSUED_AT, decoded.getIssuedAt().toInstant());
        assertEquals(ISSUED_AT.plusSeconds(3600), decoded.getExpiresAt().toInstant());
        assertEquals(3600, issued.expiresIn());
        assertEquals(issued.expiresIn(), decoded.getExpiresAt().toInstant().getEpochSecond()
                - decoded.getIssuedAt().toInstant().getEpochSecond());
    }

    @Test
    void rejectsMissingOrShortSigningSecret() {
        assertThrows(IllegalArgumentException.class, () -> new TokenService(null, Clock.systemUTC()));
        assertThrows(IllegalArgumentException.class, () -> new TokenService("too-short", Clock.systemUTC()));
    }
}