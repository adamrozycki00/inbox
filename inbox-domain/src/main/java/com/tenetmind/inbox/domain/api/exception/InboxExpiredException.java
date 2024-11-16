package com.tenetmind.inbox.domain.api.exception;

import org.zalando.problem.AbstractThrowableProblem;

import static org.zalando.problem.Status.BAD_REQUEST;

public class InboxExpiredException extends AbstractThrowableProblem {
    public InboxExpiredException() {
        super(null, "Inbox expired", BAD_REQUEST, "Requested inbox expired and cannot accept replies");
    }
}
