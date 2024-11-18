package com.tenetmind.inbox.infra.adapter.restapi;

import com.tenetmind.inbox.domain.api.model.InboxModel;
import com.tenetmind.inbox.domain.api.model.MessageModel;
import com.tenetmind.inbox.domain.api.port.in.CreateInboxUseCase;
import com.tenetmind.inbox.domain.api.port.in.GetInboxContentUseCase;
import com.tenetmind.inbox.domain.api.port.in.ReplyToInboxUseCase;
import com.tenetmind.inbox.infra.adapter.restapi.model.CreateInboxRequest;
import com.tenetmind.inbox.infra.adapter.restapi.model.CreateInboxResponse;
import com.tenetmind.inbox.infra.adapter.restapi.model.InboxMessagesResponse;
import com.tenetmind.inbox.infra.adapter.restapi.model.InboxResponse;
import com.tenetmind.inbox.infra.adapter.restapi.model.ReplyToInboxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inboxes")
public class InboxController {

  private final CreateInboxUseCase createInboxUseCase;
  private final ReplyToInboxUseCase replyToInboxUseCase;
  private final GetInboxContentUseCase getInboxContentUseCase;

  @PostMapping
  @ResponseStatus(CREATED)
  public CreateInboxResponse createInbox(@RequestBody CreateInboxRequest request) {
    CreateInboxUseCase.Command command = request.toCommand();
    InboxModel inbox = createInboxUseCase.createInbox(command);
    String id = inbox.getId();
    return CreateInboxResponse.with(id);
  }

  @PostMapping("{id}:reply")
  @ResponseStatus(NO_CONTENT)
  public void reply(@PathVariable String id, @RequestBody ReplyToInboxRequest request) {
    ReplyToInboxUseCase.Command command = request.toCommandWithId(id);
    replyToInboxUseCase.replyToInbox(command);
  }

  @GetMapping("{id}")
  @ResponseStatus(OK)
  public InboxResponse getInbox(@PathVariable String id) {
    GetInboxContentUseCase.GetInboxQuery query = GetInboxContentUseCase.GetInboxQuery.withId(id);
    InboxModel inbox = getInboxContentUseCase.getInbox(query);
    return InboxResponse.with(inbox);
  }

  @GetMapping("{id}/messages")
  @ResponseStatus(OK)
  public InboxMessagesResponse getInboxMessages(@PathVariable String id,
                                                @RequestParam String username,
                                                @RequestParam String secret) {
    var query = GetInboxContentUseCase.GetMessagesQuery.builder()
        .inboxId(id)
        .username(username)
        .secret(secret)
        .build();
    List<? extends MessageModel> messages = getInboxContentUseCase.getMessages(query);
    return InboxMessagesResponse.fromModels(messages);
  }
}
