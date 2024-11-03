package org.acme.inbox.domain.port.out;

import org.acme.inbox.domain.model.Inbox;

public interface SaveInboxPort {

    void save(Inbox inbox);
}
