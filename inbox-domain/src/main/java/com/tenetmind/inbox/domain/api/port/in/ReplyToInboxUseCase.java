package com.tenetmind.inbox.domain.api.port.in;

import lombok.Builder;

public interface ReplyToInboxUseCase {

  void replyToInbox(Command command);

  @Builder
  record Command(String inboxId, String messageBody, String username, String secret) {
  }
}
