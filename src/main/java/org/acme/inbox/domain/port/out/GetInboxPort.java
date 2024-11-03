package org.acme.inbox.domain.port.out;

import org.acme.inbox.domain.model.Inbox;

public interface GetInboxPort {

    Inbox getInbox(String inboxId);
}
