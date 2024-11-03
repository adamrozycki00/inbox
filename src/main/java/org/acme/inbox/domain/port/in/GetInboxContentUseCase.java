package org.acme.inbox.domain.port.in;

import lombok.Builder;
import org.acme.inbox.domain.model.Inbox;
import org.acme.inbox.domain.model.Message;

import java.util.List;

public interface GetInboxContentUseCase {

    Inbox.Info getInboxInfo(InfoQuery query);

    List<Message> getInboxMessages(MessagesQuery query);

    @Builder
    record MessagesQuery(String inboxId, String username, String secret) {
    }

    record InfoQuery(String inboxId) {
        public static InfoQuery withId(String inboxId) {
            return new InfoQuery(inboxId);
        }
    }
}
