package com.tenetmind.inbox.domain.model;

import lombok.Builder;
import lombok.Getter;
import com.tenetmind.inbox.domain.api.model.MessageModel;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.isNull;

@Getter
@Builder
public class Message implements MessageModel {

    private final String body;
    private final Instant createdAt;
    private final String signature;

    public boolean isAnonymous() {
        return isNull(signature);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageModel messageModel)) return false;
        return Objects.equals(body, messageModel.getBody())
                && Objects.equals(createdAt, messageModel.getCreatedAt())
                && Objects.equals(signature, messageModel.getSignature());
    }
}
