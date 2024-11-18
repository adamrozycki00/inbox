package com.tenetmind.inbox.infra.adapter.restapi.model;

import com.tenetmind.inbox.domain.api.model.InboxModel;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class Inbox implements InboxModel {

  String id;
  String topic;
  String ownerSignature;
  LocalDate expirationDate;
  boolean anonSubmissions;
}
