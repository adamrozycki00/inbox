package com.tenetmind.inbox.infra.adapter.restapi.model;

import lombok.Builder;
import com.tenetmind.inbox.domain.api.port.in.ReplyToInboxUseCase;

@Builder
public record ReplyToInboxRequest(String username, String secret, String messageBody) {

    public ReplyToInboxUseCase.Command toCommandWithId(String inboxId) {
        return ReplyToInboxUseCase.Command.builder()
                .username(this.username)
                .secret(this.secret)
                .inboxId(inboxId)
                .messageBody(this.messageBody)
                .build();
    }
}
