package com.tenetmind.inbox.infra.adapter.restapi.model;

import com.tenetmind.inbox.domain.api.port.in.ReplyToInboxUseCase;
import lombok.Builder;

@Builder
public record ReplyToInboxRequest(String username, String secret, String messageBody) {

  public ReplyToInboxUseCase.Command toCommandWithId(String inboxId) {
    return ReplyToInboxUseCase.Command.builder()
        .username(this.username)
        .secret(this.secret)
        .inboxId(inboxId)
        .messageBody(this.messageBody)
        .build();
  }
}
