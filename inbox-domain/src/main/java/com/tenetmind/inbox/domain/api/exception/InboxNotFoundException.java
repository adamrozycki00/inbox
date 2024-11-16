package com.tenetmind.inbox.domain.api.exception;

import org.zalando.problem.AbstractThrowableProblem;

import static org.zalando.problem.Status.NOT_FOUND;

public class InboxNotFoundException extends AbstractThrowableProblem {
    public InboxNotFoundException() {
        super(null, "Inbox not found", NOT_FOUND, "Requested inbox does not exist");
    }
}
