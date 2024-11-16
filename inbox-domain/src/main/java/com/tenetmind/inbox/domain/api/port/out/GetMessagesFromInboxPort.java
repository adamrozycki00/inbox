package com.tenetmind.inbox.domain.api.port.out;

import com.tenetmind.inbox.domain.api.model.MessageModel;

import java.util.List;

public interface GetMessagesFromInboxPort {

    List<? extends MessageModel> getByInboxId(String inboxId);
}
