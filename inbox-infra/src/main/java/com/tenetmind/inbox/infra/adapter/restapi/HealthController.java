package com.tenetmind.inbox.infra.adapter.restapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/")
public class HealthController {

  @GetMapping("custom-health")
  @ResponseStatus(OK)
  public String health() {
    return "OK\n";
  }
}
