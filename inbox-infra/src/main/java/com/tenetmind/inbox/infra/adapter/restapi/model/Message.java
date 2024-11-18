package com.tenetmind.inbox.infra.adapter.restapi.model;

import com.tenetmind.inbox.domain.api.model.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message implements MessageModel {

  private String body;
  private Instant createdAt;
  private String signature;
}
