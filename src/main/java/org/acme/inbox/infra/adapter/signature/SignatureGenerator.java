package org.acme.inbox.infra.adapter.signature;

import lombok.RequiredArgsConstructor;
import org.acme.inbox.domain.api.port.out.GenerateSignaturePort;
import org.apache.commons.codec.digest.DigestUtils;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class SignatureGenerator implements GenerateSignaturePort {

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
