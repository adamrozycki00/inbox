package com.tenetmind.inbox.domain.model;

import com.tenetmind.inbox.domain.api.model.MessageModel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

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
}
