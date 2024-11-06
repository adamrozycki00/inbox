package org.acme.inbox.infra.adapter.db.model;

import lombok.Builder;
import lombok.Value;
import org.acme.inbox.domain.api.model.InboxModel;

import java.time.LocalDate;
import java.util.Objects;

@Value
@Builder
public class Inbox implements InboxModel {

    String id;
    String topic;
    String ownerSignature;
    LocalDate expirationDate;
    boolean anonSubmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InboxModel inboxModel)) return false;
        return Objects.equals(id, inboxModel.getId());
    }
}
