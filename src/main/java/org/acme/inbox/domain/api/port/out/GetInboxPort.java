package org.acme.inbox.domain.api.port.out;

import org.acme.inbox.domain.api.model.InboxModel;

public interface GetInboxPort {

    InboxModel getById(String inboxId);
}
