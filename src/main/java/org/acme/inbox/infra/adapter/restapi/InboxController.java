package org.acme.inbox.infra.adapter.restapi;

import lombok.RequiredArgsConstructor;
import org.acme.inbox.domain.model.Inbox;
import org.acme.inbox.domain.model.Message;
import org.acme.inbox.domain.port.in.CreateInboxUseCase;
import org.acme.inbox.domain.port.in.GetInboxContentUseCase;
import org.acme.inbox.domain.port.in.ReplyToInboxUseCase;
import org.acme.inbox.infra.adapter.restapi.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

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
        Inbox inbox = createInboxUseCase.createInbox(command);
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
    public InboxInfoResponse getInboxInfo(@PathVariable String id) {
        GetInboxContentUseCase.InfoQuery query = GetInboxContentUseCase.InfoQuery.withId(id);
        Inbox.Info inboxInfo = getInboxContentUseCase.getInboxInfo(query);
        return InboxInfoResponse.with(inboxInfo);
    }

    @GetMapping("{id}/messages")
    @ResponseStatus(OK)
    public InboxMessagesResponse getInboxMessages(@PathVariable String id,
                                                  @RequestParam String username,
                                                  @RequestParam String secret) {
        var query = GetInboxContentUseCase.MessagesQuery.builder()
                .inboxId(id)
                .username(username)
                .secret(secret)
                .build();
        List<Message> messages = getInboxContentUseCase.getInboxMessages(query);
        return InboxMessagesResponse.with(messages);
    }
}
