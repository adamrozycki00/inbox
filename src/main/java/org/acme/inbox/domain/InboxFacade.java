package org.acme.inbox.domain;

import lombok.RequiredArgsConstructor;
import org.acme.inbox.domain.api.exception.InboxNotFoundException;
import org.acme.inbox.domain.api.model.InboxModel;
import org.acme.inbox.domain.api.model.MessageModel;
import org.acme.inbox.domain.api.port.in.CreateInboxUseCase;
import org.acme.inbox.domain.api.port.in.GetInboxContentUseCase;
import org.acme.inbox.domain.api.port.in.ReplyToInboxUseCase;
import org.acme.inbox.domain.api.port.out.GenerateSignaturePort;
import org.acme.inbox.domain.api.port.out.GetInboxPort;
import org.acme.inbox.domain.api.port.out.GetMessagesFromInboxPort;
import org.acme.inbox.domain.api.port.out.SaveInboxPort;
import org.acme.inbox.domain.api.port.out.SaveMessageToInboxPort;
import org.acme.inbox.domain.model.Inbox;
import org.acme.inbox.domain.model.Message;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Map.entry;

@RequiredArgsConstructor
public class InboxFacade implements CreateInboxUseCase, ReplyToInboxUseCase, GetInboxContentUseCase {

    private final SaveInboxPort saveInboxPort;
    private final GetInboxPort getInboxPort;
    private final SaveMessageToInboxPort saveMessageToInboxPort;
    private final GetMessagesFromInboxPort getMessagesFromInboxPort;
    private final GenerateSignaturePort generateSignaturePort;

    @Override
    public InboxModel createInbox(CreateInboxUseCase.Command command) {
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
    public void replyToInbox(ReplyToInboxUseCase.Command command) {
        var userSignature = generateSignaturePort.generate(command.username(), command.secret());
        Message message = Message.builder()
                .body(command.messageBody())
                .signature(userSignature)
                .createdAt(Instant.now())
                .build();
        var inboxModel = getInboxPort.findById(command.inboxId()).orElseThrow(InboxNotFoundException::new);
        Inbox inbox = Inbox.fromModel(inboxModel);
        inbox.validateMessageAllowed(message);
        var inboxIdAndMessage = entry(command.inboxId(), (MessageModel) message);
        saveMessageToInboxPort.save(inboxIdAndMessage);
    }

    @Override
    public InboxModel getInbox(GetInboxQuery query) {
        return getInboxPort.findById(query.inboxId()).orElseThrow(InboxNotFoundException::new);
    }

    @Override
    public List<? extends MessageModel> getMessages(GetMessagesQuery query) {
        var inboxModel = getInboxPort.findById(query.inboxId()).orElseThrow(InboxNotFoundException::new);
        var inbox = Inbox.fromModel(inboxModel);
        String userSignature = generateSignaturePort.generate(query.username(), query.secret());

        if (inbox.isOwnedBy(userSignature)) {
            return getMessagesFromInboxPort.getByInboxId(inbox.getId());
        }

        return emptyList();
    }
}
