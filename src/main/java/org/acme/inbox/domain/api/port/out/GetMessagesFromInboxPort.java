package org.acme.inbox.domain.api.port.out;

import org.acme.inbox.domain.api.model.MessageModel;

import java.util.List;

public interface GetMessagesFromInboxPort {

    List<? extends MessageModel> getByInboxId(String inboxId);
}
