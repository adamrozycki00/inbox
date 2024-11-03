package org.acme.inbox.infra.bean;

import org.acme.inbox.domain.port.in.GetInboxContentUseCase;
import org.acme.inbox.domain.port.in.CreateInboxUseCase;
import org.acme.inbox.domain.port.in.ReplyToInboxUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnitTestConfig {

    @MockBean
    public CreateInboxUseCase createInboxUseCaseMock;

    @MockBean
    public ReplyToInboxUseCase replyToInboxUseCaseMock;

    @MockBean
    public GetInboxContentUseCase getInboxContentUseCaseMock;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}