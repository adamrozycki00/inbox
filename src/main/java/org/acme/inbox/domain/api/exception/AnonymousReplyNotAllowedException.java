package org.acme.inbox.domain.api.exception;

import org.zalando.problem.AbstractThrowableProblem;

import static org.zalando.problem.Status.BAD_REQUEST;

public class AnonymousReplyNotAllowedException extends AbstractThrowableProblem {

    public AnonymousReplyNotAllowedException() {
        super(null, "Anonymous reply not allowed", BAD_REQUEST, "Requested inbox does not accept anonymous submissions");
    }
}
