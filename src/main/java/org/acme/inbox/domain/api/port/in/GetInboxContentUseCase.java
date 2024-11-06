package org.acme.inbox.domain.api.port.in;

import lombok.Builder;
import org.acme.inbox.domain.api.model.InboxModel;
import org.acme.inbox.domain.api.model.MessageModel;

import java.util.List;

public interface GetInboxContentUseCase {

    InboxModel getInbox(GetInboxQuery query);

    List<? extends MessageModel> getMessages(GetMessagesQuery query);

    @Builder
    record GetMessagesQuery(String inboxId, String username, String secret) {
    }

    record GetInboxQuery(String inboxId) {
        public static GetInboxQuery withId(String inboxId) {
            return new GetInboxQuery(inboxId);
        }
    }
}
