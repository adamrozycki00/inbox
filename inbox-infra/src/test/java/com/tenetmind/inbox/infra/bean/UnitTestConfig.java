package com.tenetmind.inbox.infra.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tenetmind.inbox.domain.api.port.in.CreateInboxUseCase;
import com.tenetmind.inbox.domain.api.port.in.GetInboxContentUseCase;
import com.tenetmind.inbox.domain.api.port.in.ReplyToInboxUseCase;
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
