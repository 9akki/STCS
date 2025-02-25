package com.itheima.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EtherpadConfig {

    @Value("${etherpad.url.ipAndPort}")
    private String etherpadUrl;

    @Value("${etherpad.url.createAuthorIfNotExistsFor}")
    private String createAuthorIfNotExistsFor;

    @Value("${etherpad.url.createSession}")
    private String createSession;

    @Value("${etherpad.url.createGroupIfNotExistsFor}")
    private String createGroupIfNotExistsFor;

    @Value("${etherpad.apikey}")
    private String apikey;

    @Value("${etherpad.url.createGroupPad}")
    private String createGroupPad;

    @Value("${etherpad.url.getReadOnlyID}")
    private String getReadOnlyID;
}
