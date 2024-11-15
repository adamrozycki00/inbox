package org.acme.inbox.infra.adapter.restapi.model;

import lombok.Builder;
import lombok.Value;
import org.acme.inbox.domain.api.model.InboxModel;

import java.time.LocalDate;

@Value
@Builder
public class Inbox implements InboxModel {

    String id;
    String topic;
    String ownerSignature;
    LocalDate expirationDate;
    boolean anonSubmissions;
}
