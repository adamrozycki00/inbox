package com.tenetmind.inbox.domain;

import com.tenetmind.inbox.infra.adapter.signature.SignatureGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureGeneratorTest {

    static final String SEPARATOR = ":";
    static final String SALT = "salt";
    static final String SHA256_REGEX = "[a-fA-F0-9]{64}";

    SignatureGenerator underTest;

    @BeforeEach
    void setUp() {
        underTest = new SignatureGenerator(SEPARATOR, SALT);
    }

    @Test
    void shouldGenerateSignature() {
        // when
        String signature = underTest.generate("username", "secret");

        // then
        assertNotNull(signature);
    }

    @Test
    void shouldGenerateSignatureStartingWithUsernameAndSeparator() {
        // when
        String signature = underTest.generate("username", "secret");

        // then
        assertTrue(signature.startsWith("username" + SEPARATOR));
    }

    @Test
    void shouldGenerateSignatureContainingSha256Hash() {
        // when
        String signature = underTest.generate("username", "secret");

        // then
        String resultHash = signature.split(SEPARATOR)[1];
        assertThat(resultHash).matches(SHA256_REGEX);
    }

    @ParameterizedTest
    @MethodSource("provideNullCredentials")
    void shouldReturnNullWhenCredentialsAreNull(String username, String secret) {
        // when
        String signature = underTest.generate(username, secret);

        // then
        assertThat(signature).isNull();
    }

    static Object[][] provideNullCredentials() {
        return new Object[][]{
                {null, "secret"},
                {"username", null},
                {null, null}
        };
    }
}
