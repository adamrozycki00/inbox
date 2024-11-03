package org.acme.inbox.infra.adapter.restapi.model;

import org.acme.inbox.domain.model.Message;

import java.util.List;

public record InboxMessagesResponse(List<Message> messages) {

    public static InboxMessagesResponse with(List<Message> messages) {
        return new InboxMessagesResponse(messages);
    }
}
