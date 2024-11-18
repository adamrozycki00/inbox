package com.tenetmind.inbox.domain.api.port.out;

import com.tenetmind.inbox.domain.api.model.InboxModel;

import java.util.Optional;

public interface GetInboxPort {

  Optional<InboxModel> findById(String inboxId);
}
