package com.tenetmind.inbox.domain;

import com.tenetmind.inbox.domain.api.exception.AnonymousReplyNotAllowedException;
import com.tenetmind.inbox.domain.api.exception.InboxExpiredException;
import com.tenetmind.inbox.domain.api.exception.InboxNotFoundException;
import com.tenetmind.inbox.domain.api.model.InboxModel;
import com.tenetmind.inbox.domain.api.model.MessageModel;
import com.tenetmind.inbox.domain.api.port.in.CreateInboxUseCase;
import com.tenetmind.inbox.domain.api.port.in.GetInboxContentUseCase;
import com.tenetmind.inbox.domain.api.port.in.ReplyToInboxUseCase;
import com.tenetmind.inbox.domain.api.port.out.GetInboxPort;
import com.tenetmind.inbox.domain.api.port.out.GetMessagesFromInboxPort;
import com.tenetmind.inbox.domain.api.port.out.SaveInboxPort;
import com.tenetmind.inbox.domain.api.port.out.SaveMessageToInboxPort;
import com.tenetmind.inbox.domain.fixture.Inbox;
import com.tenetmind.inbox.domain.fixture.Message;
import com.tenetmind.inbox.infra.adapter.db.InboxDbAdapter;
import com.tenetmind.inbox.infra.adapter.db.MessageDbAdapter;
import com.tenetmind.inbox.infra.adapter.signature.SignatureGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static java.util.Collections.emptyList;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class InboxFacadeTest {

  InboxDbAdapter inboxDbAdapter = new InboxDbAdapter();
  MessageDbAdapter messageDbAdapter = new MessageDbAdapter();
  SignatureGenerator signatureGenerator = new SignatureGenerator(":", "abcdef");

  SaveInboxPort saveInboxPort = inboxDbAdapter;
  GetInboxPort getInboxPort = inboxDbAdapter;
  SaveMessageToInboxPort saveMessageToInboxPort = messageDbAdapter;
  GetMessagesFromInboxPort getMessagesFromInboxPort = messageDbAdapter;

  InboxFacade underTest;

  @BeforeEach
  void setUp() {
    underTest = new InboxFacade(saveInboxPort, getInboxPort, saveMessageToInboxPort, getMessagesFromInboxPort, signatureGenerator);
  }

  @Test
  void shouldCreateInbox() {
    // given
    var expectedInbox = Inbox.builder()
        .topic("my topic")
        .expirationDate(LocalDate.now().plusDays(30))
        .anonSubmissions(true)
        .build();
    var command = CreateInboxUseCase.Command.builder()
        .topic("my topic")
        .username("owner")
        .secret("owner secret")
        .daysToExpire(30)
        .anonSubmissions(true)
        .build();

    // when
    var resultInbox = underTest.createInbox(command);

    // then
    assertThat(resultInbox)
        .usingRecursiveComparison().ignoringFields("id", "ownerSignature", "messages")
        .isEqualTo(expectedInbox);
  }

  @Test
  void shouldSaveCreatedInbox() {
    // given
    var command = CreateInboxUseCase.Command.builder()
        .topic("my topic")
        .username("owner")
        .secret("owner secret")
        .daysToExpire(30)
        .anonSubmissions(true)
        .build();

    // when
    var created = underTest.createInbox(command);

    // then
    InboxModel saved = inboxDbAdapter.findById(created.getId()).orElseThrow();
    assertThat(created).isEqualTo(saved);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -30})
  void shouldNotCreateInboxWithPassedExpirationDate(int daysToExpire) {
    // when
    var caught = catchException(() -> CreateInboxUseCase.Command.builder()
        .topic("my topic")
        .username("owner")
        .secret("owner secret")
        .daysToExpire(daysToExpire)
        .anonSubmissions(true)
        .build());

    // then
    assertThat(caught).isInstanceOf(CreateInboxUseCase.PassedExpirationDateException.class);
  }

  @Test
  void shouldSaveReplyToCorrectInbox() {
    // given that an inbox exists
    var inbox = Inbox.builder()
        .id("inbox id")
        .expirationDate(LocalDate.now().plusDays(30))
        .build();
    saveInboxPort.save(inbox);
    var command = ReplyToInboxUseCase.Command.builder()
        .inboxId(inbox.getId())
        .username("user")
        .secret("user secret")
        .messageBody("my message")
        .build();

    // when replying to that inbox
    underTest.replyToInbox(command);

    // then the message is saved to the same inbox
    var savedToInbox = getMessagesFromInboxPort.getByInboxId(inbox.getId());
    assertThat(savedToInbox.stream().map(MessageModel::getBody)).anyMatch("my message"::equals);
  }

  @Test
  void shouldThrowWhenReplyingToInboxAfterItsExpirationDate() {
    // given that an inbox expired
    var inbox = Inbox.builder()
        .expirationDate(LocalDate.now().minusDays(4))
        .build();
    saveInboxPort.save(inbox);
    var command = ReplyToInboxUseCase.Command.builder()
        .inboxId(inbox.getId())
        .username("user")
        .secret("user secret")
        .messageBody("my message")
        .build();

    // when replying to that inbox
    var caught = catchException(() -> underTest.replyToInbox(command));

    // then the correct exception is thrown
    assertThat(caught).isInstanceOf(InboxExpiredException.class);
  }

  @Test
  void shouldAcceptAnonymousReplyIfAnonSubmissionsSet() {
    // given that an inbox can accept anonymous submissions
    var inbox = Inbox.builder()
        .id("inbox id")
        .expirationDate(LocalDate.now().plusDays(30))
        .anonSubmissions(true)
        .build();
    saveInboxPort.save(inbox);
    var anonymousCommand = ReplyToInboxUseCase.Command.builder()
        .inboxId(inbox.getId())
        .username(null)
        .secret(null)
        .messageBody("my message")
        .build();

    // when an anonymous user replies to that inbox
    underTest.replyToInbox(anonymousCommand);

    // then the message is saved to the inbox
    var savedToInbox = getMessagesFromInboxPort.getByInboxId(inbox.getId());
    assertThat(savedToInbox.stream().map(MessageModel::getBody)).anyMatch("my message"::equals);
  }

  @Test
  void shouldNotAcceptAnonymousReplyIfAnonSubmissionsNotSet() {
    // given that an inbox cannot accept anonymous submissions
    var inbox = Inbox.builder()
        .expirationDate(LocalDate.now().plusDays(30))
        .anonSubmissions(false)
        .build();
    saveInboxPort.save(inbox);
    var anonymousCommand = ReplyToInboxUseCase.Command.builder()
        .inboxId(inbox.getId())
        .username(null)
        .secret(null)
        .messageBody("my message")
        .build();

    // when an anonymous user replies to that inbox
    var caught = catchException(() -> underTest.replyToInbox(anonymousCommand));

    // then
    assertThat(caught).isInstanceOf(AnonymousReplyNotAllowedException.class);
  }

  @Test
  void shouldRetrieveInbox() {
    // given that an inbox exists
    var inbox = Inbox.builder()
        .id("inbox id")
        .expirationDate(LocalDate.now().plusDays(30))
        .build();
    saveInboxPort.save(inbox);
    var query = GetInboxContentUseCase.GetInboxQuery.withId("inbox id");
    var expectedInbox = (InboxModel) inbox;

    // when
    var resultInbox = underTest.getInbox(query);

    // then
    assertThat(resultInbox).isEqualTo(expectedInbox);
  }

  @Test
  void shouldReturnNotFoundWhenInboxDoesNotExist() {
    // given that an inbox does not exist
    var query = GetInboxContentUseCase.GetInboxQuery.withId("nonexistent id");

    // when
    var caught = catchException(() -> underTest.getInbox(query));

    // then
    assertThat(caught).isInstanceOf(InboxNotFoundException.class);
  }

  @Test
  void shouldRetrieveInboxMessagesWhenOwnerRequests() {
    // given that an inbox with messages exists and the owner requests them
    var createInboxCommand = CreateInboxUseCase.Command.builder()
        .username("owner")
        .secret("ownerSecret")
        .daysToExpire(30)
        .build();
    InboxModel inbox = underTest.createInbox(createInboxCommand);
    var message1 = Message.builder().body("message 1").signature("user:hash").build();
    var message2 = Message.builder().body("message 2").signature("another:hash").build();
    saveMessageToInboxPort.save(entry(inbox.getId(), message1));
    saveMessageToInboxPort.save(entry(inbox.getId(), message2));

    var query = GetInboxContentUseCase.GetMessagesQuery.builder()
        .inboxId(inbox.getId())
        .username("owner")
        .secret("ownerSecret")
        .build();

    // when
    var resultMessages = underTest.getMessages(query);

    // then
    assertThat(resultMessages.stream().map(MessageModel::getBody)).containsExactlyInAnyOrder("message 1", "message 2");
  }

  @Test
  void shouldReturnNoMessagesForNonOwner() {
    // given that an inbox with messages exists and a non-owner requests them
    var createInboxCommand = CreateInboxUseCase.Command.builder()
        .username("owner")
        .secret("ownerSecret")
        .daysToExpire(30)
        .build();
    InboxModel inbox = underTest.createInbox(createInboxCommand);
    var message1 = Message.builder().body("message 1").signature("user:hash").build();
    var message2 = Message.builder().body("message 2").signature("another:hash").build();
    saveMessageToInboxPort.save(entry(inbox.getId(), message1));
    saveMessageToInboxPort.save(entry(inbox.getId(), message2));

    var query = GetInboxContentUseCase.GetMessagesQuery.builder()
        .inboxId(inbox.getId())
        .username("nonOwner")
        .secret("nonOwnerSecret")
        .build();

    // when
    var resultMessages = underTest.getMessages(query);

    // then
    assertThat(resultMessages).isEqualTo(emptyList());
  }
}
