package com.tenetmind.inbox.infra.adapter.restapi.model;

public record CreateInboxResponse(String id) {

    public static CreateInboxResponse with(String id) {
        return new CreateInboxResponse(id);
    }
}
