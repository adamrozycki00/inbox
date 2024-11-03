package org.acme.inbox.infra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.acme.inbox.domain.model.Inbox;
import org.acme.inbox.domain.model.Message;
import org.acme.inbox.domain.port.in.CreateInboxUseCase;
import org.acme.inbox.domain.port.in.GetInboxContentUseCase;
import org.acme.inbox.domain.port.in.ReplyToInboxUseCase;
import org.acme.inbox.infra.adapter.restapi.InboxController;
import org.acme.inbox.infra.adapter.restapi.model.*;
import org.acme.inbox.infra.bean.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InboxController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
class InboxControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CreateInboxUseCase createInboxUseCaseMock;
    @Autowired
    ReplyToInboxUseCase replyToInboxUseCaseMock;
    @Autowired
    GetInboxContentUseCase getInboxContentUseCaseMock;
    @Autowired
    MockMvc mvc;

    @Test
    @SneakyThrows
    void shouldReturnCreatedWhenCreatingInbox() {
        // given
        var dummyRequest = CreateInboxRequest.builder().daysToExpire(1).build();
        Inbox dummyInbox = Inbox.builder().build();
        when(createInboxUseCaseMock.createInbox(any())).thenReturn(dummyInbox);

        // when
        mvc.perform(post("/api/inboxes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyRequest)))
                // then
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void shouldRespondWithIdOfCreatedInbox() {
        // given
        var request = CreateInboxRequest.builder()
                .username("user")
                .secret("my secret")
                .topic("topic")
                .daysToExpire(10)
                .anonSubmissions(true)
                .build();
        var expectedCommand = CreateInboxUseCase.Command.builder()
                .username("user")
                .secret("my secret")
                .topic("topic")
                .daysToExpire(10)
                .anonSubmissions(true)
                .build();
        var expectedInbox = Inbox.builder().build();
        var expectedResponse = CreateInboxResponse.with(expectedInbox.getId());
        when(createInboxUseCaseMock.createInbox(expectedCommand)).thenReturn(expectedInbox);

        // when
        String response = mvc.perform(post("/api/inboxes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // then
        var resultResponse = objectMapper.readValue(response, CreateInboxResponse.class);
        assertThat(resultResponse).isEqualTo(expectedResponse);
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestWhenCreatingInboxWithPassedExpirationDate() {
        // given
        var invalidRequest = CreateInboxRequest.builder()
                .daysToExpire(-1)
                .build();

        // when
        mvc.perform(post("/api/inboxes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldReturnNoContentWhenReplyingToInbox() {
        // given
        var dummyRequest = ReplyToInboxRequest.builder().build();

        // when
        mvc.perform(post("/api/inboxes/{id}:reply", "any-id")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyRequest)))
                // then
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void shouldCallUseCaseToReplyToInbox() {
        // given
        var request = ReplyToInboxRequest.builder()
                .username("user")
                .secret("secret")
                .messageBody("my message")
                .build();
        var expectedCommand = ReplyToInboxUseCase.Command.builder()
                .inboxId("any-id")
                .username("user")
                .secret("secret")
                .messageBody("my message")
                .build();

        // when
        mvc.perform(post("/api/inboxes/{id}:reply", "any-id")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        verify(replyToInboxUseCaseMock).replyToInbox(expectedCommand);
    }

    @Test
    @SneakyThrows
    void shouldReturnOkWhenPresentingInboxInfo() {
        // given
        when(getInboxContentUseCaseMock.getInboxInfo(any())).thenReturn(Inbox.Info.builder().build());

        // when
        mvc.perform(get("/api/inboxes/{id}", "any-id"))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldRespondWithInboxInfo() {
        // given
        var expectedInboxInfo = Inbox.Info.builder()
                .topic("topic")
                .ownerSignature("owner:signature")
                .expirationDate(LocalDate.now().plusDays(10))
                .anonSubmissions(true)
                .build();
        var expectedResponse = InboxInfoResponse.builder()
                .topic("topic")
                .ownerSignature("owner:signature")
                .expirationDate(LocalDate.now().plusDays(10))
                .anonSubmissions(true)
                .build();
        var query = GetInboxContentUseCase.InfoQuery.withId("any-id");
        when(getInboxContentUseCaseMock.getInboxInfo(query)).thenReturn(expectedInboxInfo);

        // when
        String response = mvc.perform(get("/api/inboxes/{id}", "any-id"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        var resultInfo = objectMapper.readValue(response, InboxInfoResponse.class);
        assertThat(resultInfo).isEqualTo(expectedResponse);
    }

    @Test
    @SneakyThrows
    void shouldReturnOKWhenPresentingInboxMessages() {
        // given
        when(getInboxContentUseCaseMock.getInboxMessages(any())).thenReturn(emptyList());

        // when
        mvc.perform(get("/api/inboxes/{id}/messages", "any-id")
                        .param("username", "user")
                        .param("secret", "secret"))

                // then
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldRespondWithInboxMessages() {
        // given
        var message1 = Message.builder().body("message 1").signature("user:hash").build();
        var message2 = Message.builder().body("message 2").signature("another:hash").build();
        var expectedMessages = List.of(message1, message2);
        var expectedResponse = new InboxMessagesResponse(expectedMessages);
        var expectedQuery = GetInboxContentUseCase.MessagesQuery.builder()
                .inboxId("id")
                .username("owner")
                .secret("owner-secret")
                .build();
        when(getInboxContentUseCaseMock.getInboxMessages(expectedQuery)).thenReturn(expectedMessages);

        // when
        String response = mvc.perform(get("/api/inboxes/{id}/messages", "id")
                        .param("username", "owner")
                        .param("secret", "owner-secret"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        var resultMessages = objectMapper.readValue(response, InboxMessagesResponse.class);
        assertThat(resultMessages).isEqualTo(expectedResponse);
    }
}
