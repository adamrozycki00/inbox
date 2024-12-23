package com.tenetmind.inbox.domain.api.port.in;

import com.tenetmind.inbox.domain.api.model.InboxModel;
import lombok.Builder;
import org.zalando.problem.AbstractThrowableProblem;

import java.util.Map;

import static org.zalando.problem.Status.BAD_REQUEST;

public interface CreateInboxUseCase {

  InboxModel createInbox(Command command);

  @Builder
  record Command(String username, String secret, String topic, int daysToExpire, boolean anonSubmissions) {
    public Command {
      validate(daysToExpire);
    }

    private void validate(int daysToExpire) {
      if (daysToExpire <= 0) {
        throw new PassedExpirationDateException(Map.of("daysToExpire", daysToExpire));
      }
    }
  }

  final class PassedExpirationDateException extends AbstractThrowableProblem {
    public PassedExpirationDateException(Map<String, Object> params) {
      super(null, "Passed expiration date", BAD_REQUEST,
          "Requested days to expire should be greater than zero", null, null, params);
    }
  }
}
