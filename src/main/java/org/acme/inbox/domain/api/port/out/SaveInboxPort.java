package org.acme.inbox.domain.api.port.out;

import org.acme.inbox.domain.api.model.InboxModel;

public interface SaveInboxPort {

    void save(InboxModel inbox);
}
