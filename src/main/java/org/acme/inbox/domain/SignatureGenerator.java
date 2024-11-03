package org.acme.inbox.domain;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
class SignatureGenerator {

    private final String separator;
    private final String salt;

    public String generate(String username, String secret) {
        if (isNull(username) || isNull(secret)) {
            return null;
        }

        return username + separator + hash(username, secret, salt);
    }

    private String hash(String username, String secret, String salt) {
        return DigestUtils.sha256Hex(username + secret + salt);
    }
}
