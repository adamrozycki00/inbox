package org.acme.inbox.infra.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.acme.inbox.domain.model.Message;
import org.acme.inbox.infra.adapter.restapi.InboxController;
import org.acme.inbox.infra.adapter.restapi.model.CreateInboxRequest;
import org.acme.inbox.infra.adapter.restapi.model.CreateInboxResponse;
import org.acme.inbox.infra.adapter.restapi.model.InboxInfoResponse;
import org.acme.inbox.infra.adapter.restapi.model.InboxMessagesResponse;
import org.acme.inbox.infra.bean.IntegrationTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InboxController.class)
@Import(IntegrationTestConfig.class)
class InboxIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;

    static final String USERNAME = "inbox-owner";
    static final String USER_SECRET = "owner-secret";
    static final String TOPIC = "topic";
    static final int DAYS_TO_EXPIRE = 10;
    static final LocalDate EXPIRATION_DATE = LocalDate.now().plusDays(DAYS_TO_EXPIRE);

    String createdInboxId;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        // create inbox
        var createInboxRequest = CreateInboxRequest.builder()
                .username(USERNAME)
                .secret(USER_SECRET)
                .topic(TOPIC)
                .daysToExpire(DAYS_TO_EXPIRE)
                .build();
        String responseStr = mvc.perform(post("/api/inboxes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInboxRequest)))
                .andReturn().getResponse().getContentAsString();
        createdInboxId = objectMapper.readValue(responseStr, CreateInboxResponse.class).id();

        // add two messages
        mvc.perform(post("/api/inboxes/{id}:reply", createdInboxId)
                .contentType(APPLICATION_JSON)
                .content(""" 
                        {
                          "username": "user",
                          "secret": "user-secret",
                          "messageBody": "My first message!"
                        }"""));
        mvc.perform(post("/api/inboxes/{id}:reply", createdInboxId)
                .contentType(APPLICATION_JSON)
                .content(""" 
                        {
                          "username": "another",
                          "secret": "another-secret",
                          "messageBody": "Another message!"
                        }"""));
    }

    @Test
    @SneakyThrows
    void shouldReturnInboxInfo() {
        // given
        var expectedResponse = InboxInfoResponse.builder()
                .topic(TOPIC)
                .expirationDate(EXPIRATION_DATE)
                .anonSubmissions(false)
                .build();

        // when
        String responseStr = mvc.perform(get("/api/inboxes/{id}", createdInboxId))
                .andReturn().getResponse().getContentAsString();

        // then
        var resultResponse = objectMapper.readValue(responseStr, InboxInfoResponse.class);
        assertThat(resultResponse).usingRecursiveComparison().ignoringFields("ownerSignature").isEqualTo(expectedResponse);
    }

    @Test
    @SneakyThrows
    void shouldReturnMessages() {
        // given
        var expectedMessageBodies = List.of("My first message!", "Another message!");

        // when
        String responseStr = mvc.perform(get("/api/inboxes/{id}/messages", createdInboxId)
                        .param("username", USERNAME)
                        .param("secret", USER_SECRET))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        var resultResponse = objectMapper.readValue(responseStr, InboxMessagesResponse.class);
        var resultMessageBodies = resultResponse.messages().stream()
                .map(Message::body)
                .toList();
        assertThat(resultMessageBodies).containsExactlyInAnyOrderElementsOf(expectedMessageBodies);
    }
}
