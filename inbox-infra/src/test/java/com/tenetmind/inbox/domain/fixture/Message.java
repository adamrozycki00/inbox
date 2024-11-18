package com.tenetmind.inbox.domain.fixture;

import com.tenetmind.inbox.domain.api.model.MessageModel;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Objects;

@Value
@Builder
public class Message implements MessageModel {

  String body;
  Instant createdAt;
  String signature;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MessageModel messageModel)) return false;
    return Objects.equals(body, messageModel.getBody())
        && Objects.equals(createdAt, messageModel.getCreatedAt())
        && Objects.equals(signature, messageModel.getSignature());
  }
}
