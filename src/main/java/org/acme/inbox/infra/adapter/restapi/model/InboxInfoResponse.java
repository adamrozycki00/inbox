package org.acme.inbox.infra.adapter.restapi.model;

import lombok.Builder;
import org.acme.inbox.domain.model.Inbox;

import java.time.LocalDate;

@Builder
public record InboxInfoResponse(String topic, String ownerSignature, LocalDate expirationDate,
                                boolean anonSubmissions) {

    public static InboxInfoResponse with(Inbox.Info info) {
        return new InboxInfoResponse(info.topic(), info.ownerSignature(), info.expirationDate(), info.anonSubmissions());
    }
}
