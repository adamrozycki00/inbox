package org.acme.inbox.infra.adapter.restapi.model;

import lombok.Builder;
import org.acme.inbox.domain.api.model.InboxModel;

import java.time.LocalDate;

@Builder
public record InboxResponse(String topic, String ownerSignature, LocalDate expirationDate,
                            boolean anonSubmissions) {

    public static InboxResponse with(InboxModel inbox) {
        return new InboxResponse(inbox.getTopic(), inbox.getOwnerSignature(), inbox.getExpirationDate(), inbox.isAnonSubmissions());
    }
}
