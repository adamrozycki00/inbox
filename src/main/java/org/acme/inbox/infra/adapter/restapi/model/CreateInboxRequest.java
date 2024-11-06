package org.acme.inbox.infra.adapter.restapi.model;

import lombok.Builder;
import org.acme.inbox.domain.api.port.in.CreateInboxUseCase;

@Builder
public record CreateInboxRequest(String username, String secret, String topic, int daysToExpire,
                                 boolean anonSubmissions) {

    public CreateInboxUseCase.Command toCommand() {
        return CreateInboxUseCase.Command.builder()
                .username(this.username)
                .secret(this.secret)
                .topic(this.topic)
                .daysToExpire(this.daysToExpire)
                .anonSubmissions(this.anonSubmissions)
                .build();
    }
}
