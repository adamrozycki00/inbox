package com.tenetmind.inbox.domain.model;

import com.tenetmind.inbox.domain.api.exception.AnonymousReplyNotAllowedException;
import com.tenetmind.inbox.domain.api.exception.InboxExpiredException;
import com.tenetmind.inbox.domain.api.model.InboxModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
public class Inbox implements InboxModel {

  @Builder.Default
  private final String id = UUID.randomUUID().toString();
  private final String topic;
  private final String ownerSignature;
  @Setter
  private LocalDate expirationDate;
  private final boolean anonSubmissions;
  private final List<Message> messages = new ArrayList<>();

  public static Inbox fromModel(InboxModel inboxModel) {
    return Inbox.builder()
        .id(inboxModel.getId())
        .topic(inboxModel.getTopic())
        .ownerSignature(inboxModel.getOwnerSignature())
        .expirationDate(inboxModel.getExpirationDate())
        .anonSubmissions(inboxModel.isAnonSubmissions())
        .build();
  }

  public boolean isOwnedBy(String signature) {
    return ownerSignature.equals(signature);
  }

  public void validateMessageAllowed(Message message) {
    if (this.isExpired()) {
      throw new InboxExpiredException();
    }

    if (message.isAnonymous() && !anonSubmissions) {
      throw new AnonymousReplyNotAllowedException();
    }
  }

  private boolean isExpired() {
    return expirationDate.isBefore(LocalDate.now());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof InboxModel inboxModel)) return false;
    return Objects.equals(id, inboxModel.getId());
  }
}
