package com.sample.sample.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samples.sample.CodeApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

@ActiveProfiles("it")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CodeApplication.class)
@ContextConfiguration(initializers = TestcontainerInitializer.class)
public abstract class BaseIntegrationTest implements DBCleaner {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Autowired
    protected SecurityProperties securityProperties;

    protected Consumer<HttpHeaders> authHeaders() {
        return httpHeaders -> {
            // refactor me when authorization is implemented (or disabled)
            httpHeaders.add(HttpHeaders.AUTHORIZATION,
                    "basic " + Base64.getEncoder().encodeToString(
                            (securityProperties.getUser().getName() + ":" +
                                    securityProperties.getUser().getPassword())
                                    .getBytes(StandardCharsets.UTF_8)));
        };
    }
}
