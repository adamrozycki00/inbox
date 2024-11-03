package org.acme.inbox.domain;

import org.acme.inbox.domain.model.Inbox;
import org.acme.inbox.domain.model.Message;
import org.acme.inbox.domain.model.exception.AnonymousReplyNotAllowedException;
import org.acme.inbox.domain.model.exception.InboxExpiredException;
import org.acme.inbox.domain.port.in.CreateInboxUseCase;
import org.acme.inbox.domain.port.in.GetInboxContentUseCase;
import org.acme.inbox.domain.port.in.ReplyToInboxUseCase;
import org.acme.inbox.domain.port.out.GetInboxPort;
import org.acme.inbox.domain.port.out.SaveInboxPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InboxFacadeTest {

    @Mock
    SaveInboxPort saveInboxPort;
    @Mock
    GetInboxPort getInboxPort;
    @Mock
    SignatureGenerator signatureGenerator;

    InboxFacade underTest;

    @BeforeEach
    void setUp() {
        underTest = new InboxFacade(saveInboxPort, getInboxPort, signatureGenerator);
    }

    @Test
    void shouldCreateInbox() {
        // given
        var expectedInbox = Inbox.builder()
                .topic("My topic")
                .ownerSignature("owner:hash")
                .expirationDate(LocalDate.now().plusDays(30))
                .anonSubmissions(true)
                .build();
        var command = CreateInboxUseCase.Command.builder()
                .topic("My topic")
                .username("owner")
                .secret("owner secret")
                .daysToExpire(30)
                .anonSubmissions(true)
                .build();
        when(signatureGenerator.generate("owner", "owner secret")).thenReturn("owner:hash");

        // when
        Inbox resultInbox = underTest.createInbox(command);

        // then
        assertThat(resultInbox).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedInbox);

        // and
        verify(saveInboxPort).save(resultInbox);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -30})
    void shouldNotCreateInboxWithPassedExpirationDate(int daysToExpire) {
        // when
        var caught = catchException(() -> CreateInboxUseCase.Command.builder()
                .topic("My topic")
                .username("owner")
                .secret("owner secret")
                .daysToExpire(daysToExpire)
                .anonSubmissions(true)
                .build());

        // then
        assertThat(caught).isInstanceOf(CreateInboxUseCase.PassedExpirationDateException.class);
    }

    @Test
    void shouldSaveReplyInCorrectInbox() {
        // given
        var inbox = Inbox.builder()
                .expirationDate(LocalDate.now().plusDays(10))
                .build();
        var command = ReplyToInboxUseCase.Command.builder()
                .inboxId(inbox.getId())
                .username("user")
                .secret("user secret")
                .messageBody("message body")
                .build();
        var expectedMessage = Message.builder()
                .body("message body")
                .signature("user:hash")
                .build();
        when(signatureGenerator.generate("user", "user secret")).thenReturn("user:hash");
        when(getInboxPort.getInbox(inbox.getId())).thenReturn(inbox);

        // when
        Message reply = underTest.replyToInbox(command);

        // then
        assertThat(reply).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(expectedMessage);
        assertThat(inbox.getMessages()).contains(reply);
    }

    @Test
    void shouldThrowWhenReplyingToInboxAfterItsExpirationDate() {
        // given
        var inbox = Inbox.builder()
                .expirationDate(LocalDate.now().minusDays(1))
                .build();
        var command = ReplyToInboxUseCase.Command.builder()
                .inboxId(inbox.getId())
                .username("user")
                .secret("user secret")
                .messageBody("message body")
                .build();
        when(signatureGenerator.generate("user", "user secret")).thenReturn("user:hash");
        when(getInboxPort.getInbox(inbox.getId())).thenReturn(inbox);

        // when
        var caught = catchException(() -> underTest.replyToInbox(command));

        // then
        assertThat(caught).isInstanceOf(InboxExpiredException.class);
    }

    @Test
    void shouldAcceptAnonymousReplyIfAnonSubmissionsSet() {
        // given
        var inbox = Inbox.builder()
                .anonSubmissions(true)
                .expirationDate(LocalDate.now().plusDays(10))
                .build();
        var command = ReplyToInboxUseCase.Command.builder()
                .inboxId(inbox.getId())
                .username(null)
                .secret(null)
                .messageBody("message body")
                .build();
        var expectedMessage = Message.builder()
                .body("message body")
                .build();
        when(signatureGenerator.generate(null, null)).thenReturn(null);
        when(getInboxPort.getInbox(inbox.getId())).thenReturn(inbox);

        // when
        Message reply = underTest.replyToInbox(command);

        // then
        assertThat(reply).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(expectedMessage);
        assertThat(inbox.getMessages()).contains(reply);
    }

    @Test
    void shouldNotAcceptAnonymousReplyIfAnonSubmissionsNotSet() {
        // given
        var inbox = Inbox.builder()
                .anonSubmissions(false)
                .expirationDate(LocalDate.now().plusDays(10))
                .build();
        var command = ReplyToInboxUseCase.Command.builder()
                .inboxId(inbox.getId())
                .username(null)
                .secret(null)
                .messageBody("message body")
                .build();
        when(getInboxPort.getInbox(inbox.getId())).thenReturn(inbox);

        // when
        var caught = catchException(() -> underTest.replyToInbox(command));

        // then
        assertThat(caught).isInstanceOf(AnonymousReplyNotAllowedException.class);
    }

    @Test
    void shouldRetrieveInboxInfoWithoutMessages() {
        // given
        var inbox = Inbox.builder()
                .topic("another topic")
                .ownerSignature("someone:hash")
                .expirationDate(LocalDate.now().plusDays(10))
                .anonSubmissions(true)
                .build();
        var query = GetInboxContentUseCase.InfoQuery.withId(inbox.getId());
        var expectedInboxInfo = Inbox.Info.builder()
                .topic("another topic")
                .ownerSignature("someone:hash")
                .expirationDate(LocalDate.now().plusDays(10))
                .anonSubmissions(true)
                .build();
        when(getInboxPort.getInbox(inbox.getId())).thenReturn(inbox);

        // when
        Inbox.Info resultInboxInfo = underTest.getInboxInfo(query);

        // then
        assertThat(resultInboxInfo).isEqualTo(expectedInboxInfo);
    }

    @Test
    void shouldRetrieveInboxMessagesForOwner() {
        // given
        var inbox = Inbox.builder()
                .topic("another topic")
                .ownerSignature("owner:hash")
                .expirationDate(LocalDate.now().plusDays(10))
                .anonSubmissions(true)
                .build();
        var query = GetInboxContentUseCase.MessagesQuery.builder()
                .inboxId(inbox.getId())
                .username("owner")
                .secret("ownerSecret")
                .build();
        when(signatureGenerator.generate("owner", "ownerSecret")).thenReturn("owner:hash");
        when(getInboxPort.getInbox(inbox.getId())).thenReturn(inbox);

        var message1 = Message.builder().body("message 1").signature("user:hash").build();
        var message2 = Message.builder().body("message 2").signature("another:hash").build();
        inbox.addMessage(message1);
        inbox.addMessage(message2);
        var expectedMessages = List.of(message1, message2);

        // when
        List<Message> resultMessages = underTest.getInboxMessages(query);

        // then
        assertThat(resultMessages).isEqualTo(expectedMessages);
    }

    @Test
    void shouldReturnNoMessagesForNonOwner() {
        // given
        var inbox = Inbox.builder()
                .topic("another topic")
                .ownerSignature("owner:hash")
                .expirationDate(LocalDate.now().plusDays(10))
                .anonSubmissions(true)
                .build();
        var query = GetInboxContentUseCase.MessagesQuery.builder()
                .inboxId(inbox.getId())
                .username("nonOwner")
                .secret("nonOwnerSecret")
                .build();
        when(signatureGenerator.generate("nonOwner", "nonOwnerSecret")).thenReturn("nonOwner:hash");
        when(getInboxPort.getInbox(inbox.getId())).thenReturn(inbox);

        var message1 = Message.builder().body("message 1").signature("user:hash").build();
        var message2 = Message.builder().body("message 2").signature("another:hash").build();
        inbox.addMessage(message1);
        inbox.addMessage(message2);

        // when
        List<Message> resultMessages = underTest.getInboxMessages(query);

        // then
        assertThat(resultMessages).isEqualTo(emptyList());
    }
}
