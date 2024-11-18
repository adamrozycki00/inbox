package com.tenetmind.inbox.infra.adapter.db;

import com.tenetmind.inbox.domain.api.model.MessageModel;
import com.tenetmind.inbox.domain.api.port.out.GetMessagesFromInboxPort;
import com.tenetmind.inbox.domain.api.port.out.SaveMessageToInboxPort;
import com.tenetmind.inbox.infra.adapter.db.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class MessageDbAdapter implements SaveMessageToInboxPort, GetMessagesFromInboxPort {

  private final Map<String, List<Message>> messagesByInboxId = new HashMap<>();

  @Override
  public void save(Map.Entry<String, MessageModel> inboxIdAndMessage) {
    var inboxId = inboxIdAndMessage.getKey();
    var messageModel = inboxIdAndMessage.getValue();

    messagesByInboxId
        .computeIfAbsent(inboxId, __ -> new ArrayList<>())
        .add(map(messageModel));
  }

  @Override
  public List<? extends MessageModel> getByInboxId(String inboxId) {
    return messagesByInboxId.getOrDefault(inboxId, emptyList());
  }

  private static Message map(MessageModel messageModel) {
    return Message.builder()
        .body(messageModel.getBody())
        .createdAt(messageModel.getCreatedAt())
        .signature(messageModel.getSignature())
        .build();
  }
}
