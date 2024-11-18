package com.tenetmind.inbox.domain.api.model;

import java.time.Instant;

public interface MessageModel {

  String getBody();

  Instant getCreatedAt();

  String getSignature();

  boolean equals(Object o);
}
