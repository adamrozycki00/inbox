package com.tenetmind.inbox.domain.api.port.out;

import com.tenetmind.inbox.domain.api.model.InboxModel;

public interface SaveInboxPort {

  void save(InboxModel inbox);
}
