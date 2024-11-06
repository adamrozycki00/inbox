package org.acme.inbox.domain.api.model;

import java.time.LocalDate;

public interface InboxModel {

    String getId();

    String getTopic();

    String getOwnerSignature();

    LocalDate getExpirationDate();

    boolean isAnonSubmissions();

    boolean equals(Object o);
}
