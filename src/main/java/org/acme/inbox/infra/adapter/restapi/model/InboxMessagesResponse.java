package org.acme.inbox.infra.adapter.restapi.model;

import org.acme.inbox.domain.api.model.MessageModel;

import java.util.List;

public record InboxMessagesResponse(List<Message> messages) {

    public static InboxMessagesResponse fromModels(List<? extends MessageModel> messageModels) {
        var messages = messageModels.stream()
                .map(mm -> Message.builder()
                        .body(mm.getBody())
                        .createdAt(mm.getCreatedAt())
                        .signature(mm.getSignature())
                        .build())
                .toList();
        return new InboxMessagesResponse(messages);
    }
}
