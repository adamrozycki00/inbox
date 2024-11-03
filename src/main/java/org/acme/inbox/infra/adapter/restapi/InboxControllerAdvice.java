package org.acme.inbox.infra.adapter.restapi;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@RestControllerAdvice
class InboxControllerAdvice implements ProblemHandling {
}
