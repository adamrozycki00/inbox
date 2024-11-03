package org.acme.inbox.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.acme.inbox.domain.model.exception.AnonymousReplyNotAllowedException;
import org.acme.inbox.domain.model.exception.InboxExpiredException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Inbox {

    @Getter
    @ToString.Include
    private final String id = UUID.randomUUID().toString();
    private final String topic;
    private final String ownerSignature;
    private final LocalDate expirationDate;
    private final boolean anonSubmissions;
    @Getter
    private final List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        validateMessageAllowed(message);
        messages.add(message);
    }

    public Info getInfo() {
        return Info.builder()
                .topic(topic)
                .ownerSignature(ownerSignature)
                .expirationDate(expirationDate)
                .anonSubmissions(anonSubmissions)
                .build();
    }

    public boolean isOwnedBy(String signature) {
        return ownerSignature.equals(signature);
    }

    private void validateMessageAllowed(Message message) {
        if (this.isExpired()) {
            throw new InboxExpiredException();
        }

        if (message.isAnonymous() && !anonSubmissions) {
            throw new AnonymousReplyNotAllowedException();
        }
    }

    private boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }

    @Builder
    public record Info(String topic, String ownerSignature, LocalDate expirationDate, boolean anonSubmissions) {
    }
}
