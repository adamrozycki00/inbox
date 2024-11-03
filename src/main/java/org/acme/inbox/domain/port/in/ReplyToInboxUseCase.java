package org.acme.inbox.domain.port.in;

import lombok.Builder;
import org.acme.inbox.domain.model.Message;

public interface ReplyToInboxUseCase {

    Message replyToInbox(Command command);

    @Builder
    record Command(String inboxId, String messageBody, String username, String secret) {
    }
}
