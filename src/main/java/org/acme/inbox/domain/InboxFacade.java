package org.acme.inbox.domain;

import lombok.RequiredArgsConstructor;
import org.acme.inbox.domain.model.Inbox;
import org.acme.inbox.domain.model.Message;
import org.acme.inbox.domain.port.in.CreateInboxUseCase;
import org.acme.inbox.domain.port.in.GetInboxContentUseCase;
import org.acme.inbox.domain.port.in.ReplyToInboxUseCase;
import org.acme.inbox.domain.port.out.GenerateSignaturePort;
import org.acme.inbox.domain.port.out.GetInboxPort;
import org.acme.inbox.domain.port.out.SaveInboxPort;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class InboxFacade implements CreateInboxUseCase, ReplyToInboxUseCase, GetInboxContentUseCase {

    private final SaveInboxPort saveInboxPort;
    private final GetInboxPort getInboxPort;
    private final GenerateSignaturePort generateSignaturePort;

    @Override
    public Inbox createInbox(CreateInboxUseCase.Command command) {
        var ownerSignature = generateSignaturePort.generate(command.username(), command.secret());
        var inbox = Inbox.builder()
                .topic(command.topic())
                .ownerSignature(ownerSignature)
                .expirationDate(LocalDate.now().plusDays(command.daysToExpire()))
                .anonSubmissions(command.anonSubmissions())
                .build();
        saveInboxPort.save(inbox);
        return inbox;
    }

    @Override
    public Message replyToInbox(ReplyToInboxUseCase.Command command) {
        var inbox = getInboxPort.getInbox(command.inboxId());
        var userSignature = generateSignaturePort.generate(command.username(), command.secret());
        var message = Message.builder()
                .body(command.messageBody())
                .signature(userSignature)
                .createdAt(Instant.now())
                .build();
        inbox.addMessage(message);
        return message;
    }

    @Override
    public Inbox.Info getInboxInfo(InfoQuery query) {
        var inbox = getInboxPort.getInbox(query.inboxId());
        return inbox.getInfo();
    }

    @Override
    public List<Message> getInboxMessages(MessagesQuery query) {
        var inbox = getInboxPort.getInbox(query.inboxId());
        String userSignature = generateSignaturePort.generate(query.username(), query.secret());

        if (inbox.isOwnedBy(userSignature)) {
            return inbox.getMessages();
        }

        return emptyList();
    }
}
