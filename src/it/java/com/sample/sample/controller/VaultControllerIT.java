package com.sample.sample.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sample.sample.config.BaseIntegrationTest;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
