package com.tenetmind.inbox.infra.adapter.db;

import com.tenetmind.inbox.domain.api.model.InboxModel;
import com.tenetmind.inbox.domain.api.port.out.GetInboxPort;
import com.tenetmind.inbox.domain.api.port.out.SaveInboxPort;
import com.tenetmind.inbox.infra.adapter.db.model.Inbox;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InboxDbAdapter implements SaveInboxPort, GetInboxPort {

  private final Map<String, Inbox> inboxes = new HashMap<>();

  @Override
  public void save(InboxModel inboxModel) {
    inboxes.put(inboxModel.getId(), map(inboxModel));
  }

  @Override
  public Optional<InboxModel> findById(String inboxId) {
    var result = inboxes.get(inboxId);
    return Optional.ofNullable(result);
  }

  private static Inbox map(InboxModel inboxModel) {
    return Inbox.builder()
        .id(inboxModel.getId())
        .topic(inboxModel.getTopic())
        .ownerSignature(inboxModel.getOwnerSignature())
        .expirationDate(inboxModel.getExpirationDate())
        .anonSubmissions(inboxModel.isAnonSubmissions())
        .build();
  }
}
