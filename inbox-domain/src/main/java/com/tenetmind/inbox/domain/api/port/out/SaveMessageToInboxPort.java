package com.tenetmind.inbox.domain.api.port.out;

import com.tenetmind.inbox.domain.api.model.MessageModel;

import java.util.Map;

public interface SaveMessageToInboxPort {

  void save(Map.Entry<String, MessageModel> inboxIdAndMessage);
}
