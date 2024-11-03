package org.acme.inbox.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.time.Instant;

import static java.util.Objects.isNull;

@Builder
public record Message(String body, Instant createdAt, String signature) {

    @JsonIgnore
    public boolean isAnonymous() {
        return isNull(signature);
    }
}
