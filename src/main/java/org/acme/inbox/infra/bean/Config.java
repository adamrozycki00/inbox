package org.acme.inbox.infra.bean;

import org.acme.inbox.domain.InboxFacade;
import org.acme.inbox.domain.api.port.out.GenerateSignaturePort;
import org.acme.inbox.infra.adapter.db.InboxDbAdapter;
import org.acme.inbox.infra.adapter.db.MessageDbAdapter;
import org.acme.inbox.infra.adapter.signature.SignatureGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${inbox.separator}")
    private String separator;

    @Value("${inbox.salt}")
    private String salt;

    @Bean
    public InboxDbAdapter inboxDbAdapter() {
        return new InboxDbAdapter();
    }

    @Bean
    public MessageDbAdapter messageDbAdapter() {
        return new MessageDbAdapter();
    }

    @Bean
    public GenerateSignaturePort generateSignaturePort() {
        return new SignatureGenerator(separator, salt);
    }

    @Bean
    public InboxFacade inboxFacade() {
        return new InboxFacade(inboxDbAdapter(), inboxDbAdapter(), messageDbAdapter(), messageDbAdapter(), generateSignaturePort());
    }
}
