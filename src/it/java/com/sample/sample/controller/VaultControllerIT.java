package com.sample.sample.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sample.sample.config.BaseIntegrationTest;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_1;

class VaultControllerIT extends BaseIntegrationTest {
    @Autowired
    VaultTemplate vaultTemplate;

    @Test
    void test_gettingFeatureFlags() {
        RequestEntity<Void> getFeatures = RequestEntity
                .get("/v1/secrets/{id}", "features")
                .headers(authHeaders())
                .build();
        ResponseEntity<FeatureFlags> exchange = testRestTemplate.exchange(getFeatures, FeatureFlags.class);
        assertThat(exchange.getStatusCode(), is(HttpStatus.OK));
        assertThat(exchange.getBody(), is(not(nullValue())));
        assertThat(exchange.getBody(), is(is(
                new FeatureFlags()
                        .setSample(new FeatureFlags.Sample()
                                .setFeatures(Map.of("abc", true))))
        ));
    }

    @Test
    void test_failsToGetUnknownFeatureFlag() {
        RequestEntity<Void> getFeatures = RequestEntity
                .get("/v1/secrets/{id}", "test_failsToGetUnknownFeatureFlag")
                .headers(authHeaders())
                .build();
        ResponseEntity<FeatureFlags> exchange = testRestTemplate.exchange(getFeatures, FeatureFlags.class);
        assertThat(exchange.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void test_readingFeatureFlags() {
        VaultResponse features = vaultTemplate.opsForKeyValue("secrets", KV_1)
                .get("apps/global/features");

        assertThat(features, is(not(nullValue())));

        Map<String, Object> requiredData = features.getRequiredData();
        FeatureFlags featureFlags = objectMapper.convertValue(requiredData, FeatureFlags.class);
        assertThat(featureFlags, is(
                new FeatureFlags()
                        .setSample(new FeatureFlags.Sample()
                                .setFeatures(Map.of("abc", true))))
        );
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class FeatureFlags {
        Sample sample;

        @Data
        @Accessors(chain = true)
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Sample {
            Map<String, Boolean> features;
        }
    }
}
