package org.acme.inbox.domain.api.port.out;

import org.acme.inbox.domain.api.model.InboxModel;

import java.util.Optional;

public interface GetInboxPort {

    Optional<InboxModel> findById(String inboxId);
}
