package org.acme.inbox.infra.adapter.db;

import lombok.extern.slf4j.Slf4j;
import org.acme.inbox.domain.model.Inbox;
import org.acme.inbox.domain.port.out.GetInboxPort;
import org.acme.inbox.domain.port.out.SaveInboxPort;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InboxDbAdapter implements SaveInboxPort, GetInboxPort {

    private final Map<String, Inbox> inboxes = new HashMap<>();

    @Override
    public void save(Inbox inbox) {
        inboxes.put(inbox.getId(), inbox);
        log.info("Saved inbox: {}", inbox);
    }

    @Override
    public Inbox getInbox(String inboxId) {
        return inboxes.get(inboxId);
    }
}
