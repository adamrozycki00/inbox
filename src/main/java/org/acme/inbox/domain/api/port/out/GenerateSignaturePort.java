package org.acme.inbox.domain.api.port.out;

public interface GenerateSignaturePort {

    String generate(String username, String secret);
}
