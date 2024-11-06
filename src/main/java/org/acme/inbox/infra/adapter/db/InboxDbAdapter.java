package org.acme.inbox.infra.adapter.db;

import org.acme.inbox.domain.api.model.InboxModel;
import org.acme.inbox.domain.api.port.out.GetInboxPort;
import org.acme.inbox.domain.api.port.out.SaveInboxPort;
import org.acme.inbox.infra.adapter.db.model.Inbox;

import java.util.HashMap;
import java.util.Map;

public class InboxDbAdapter implements SaveInboxPort, GetInboxPort {

    private final Map<String, Inbox> inboxes = new HashMap<>();

    @Override
    public void save(InboxModel inboxModel) {
        inboxes.put(inboxModel.getId(), map(inboxModel));
    }

    @Override
    public InboxModel getById(String inboxId) {
        return inboxes.get(inboxId);
    }

    private static Inbox map(InboxModel inboxModel) {
        return Inbox.builder()
                .id(inboxModel.getId())
                .topic(inboxModel.getTopic())
                .ownerSignature(inboxModel.getOwnerSignature())
                .expirationDate(inboxModel.getExpirationDate())
                .anonSubmissions(inboxModel.isAnonSubmissions())
                .build();
    }
}
