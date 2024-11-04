package org.acme.inbox.domain.port.out;

public interface GenerateSignaturePort {

    String generate(String username, String secret);
}
