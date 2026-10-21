package com.bcp.workshop.auth;

import jakarta.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class AuthResourceTest {
    private static final String TEST_TOKEN_SECRET = "workshop-test-only-signing-key-32-bytes";

    @Inject
    LegacyDatabase database;

    @Test
    void logsInWithWorkshopUser() {
        String token = given()
                .contentType("application/json")
                .body("{\"username\":\"student\",\"password\":\"Workshop123!\",\"client\":\"angular-workshop\"}")
                .when().post("/auth/login")
                .then()
                .statusCode(200)
                .body("user.username", is("student"))
                .body("user.displayName", is("Workshop Student"))
                .body("token", notNullValue())
                .body("expiresIn", is(3600))
                .extract().path("token");

            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(TEST_TOKEN_SECRET)).build().verify(token);
            assertEquals("student", decoded.getSubject());
            assertEquals("STUDENT", decoded.getClaim("role").asString());
            assertEquals("angular-workshop", decoded.getClaim("client").asString());
            assertEquals(3600, decoded.getExpiresAt().toInstant().getEpochSecond() - decoded.getIssuedAt().toInstant().getEpochSecond());
    }

    @Test
    void characterizesInvalidCredentialsAndMissingOrEmptyFields() {
        assertLoginRejected("{\"username\":\"student\",\"password\":\"wrong\",\"client\":\"angular-workshop\"}", 401);
        assertLoginRejected("{\"password\":\"Workshop123!\"}", 400);
        assertLoginRejected("{\"username\":\"\",\"password\":\"Workshop123!\"}", 400);
        assertLoginRejected("{\"username\":\"student\"}", 400);
        assertLoginRejected("{\"username\":\"student\",\"password\":\"\"}", 400);
    }

    @Test
    void rejectsUnsupportedClient() {
        String client = "unrecognized-client-sentinel";
        given()
                .contentType("application/json")
                .body("{\"username\":\"student\",\"password\":\"Workshop123!\",\"client\":\"" + client + "\"}")
                .when().post("/auth/login")
                .then()
                .statusCode(400)
                .body("token", nullValue());
    }

    @Test
    void databaseLookupFailureDoesNotLogCredentialsOrSql() {
        String usernameSentinel = "username-log-sentinel";
        String passwordSentinel = "password-log-sentinel'";
        List<String> messages = new ArrayList<>();
        Logger logger = Logger.getLogger(LegacyDatabase.class.getName());
        Handler capture = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        logger.addHandler(capture);

        try {
            assertNull(database.findByCredentials(usernameSentinel, passwordSentinel));
        } finally {
            logger.removeHandler(capture);
        }

        String output = String.join("\n", messages);
        assertTrue(output.contains("Authentication lookup failed"));
        assertFalse(output.contains(usernameSentinel));
        assertFalse(output.contains(passwordSentinel));
        assertFalse(output.contains("SELECT"));
    }

    private void assertLoginRejected(String body, int statusCode) {
        given()
                .contentType("application/json")
                .body(body)
                .when().post("/auth/login")
                .then()
                .statusCode(statusCode)
                .body("token", nullValue());
    }
}
