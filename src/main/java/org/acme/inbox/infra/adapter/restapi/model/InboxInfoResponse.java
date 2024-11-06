package org.acme.inbox.infra.adapter.restapi.model;

import lombok.Builder;
import org.acme.inbox.domain.api.model.InboxModel;

import java.time.LocalDate;

@Builder
public record InboxInfoResponse(String topic, String ownerSignature, LocalDate expirationDate,
                                boolean anonSubmissions) {

    public static InboxInfoResponse with(InboxModel inbox) {
        return new InboxInfoResponse(inbox.getTopic(), inbox.getOwnerSignature(), inbox.getExpirationDate(), inbox.isAnonSubmissions());
    }
}
