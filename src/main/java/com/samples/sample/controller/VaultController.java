package com.samples.sample.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_1;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/secrets")
public class VaultController {
    private final VaultTemplate vaultTemplate;

    // allow getting only specific known secrets
    @GetMapping("/{id}")
    Map<String, Object> secretData(@PathVariable("id") KnownSecret secret) {
        VaultResponse features = vaultTemplate.opsForKeyValue("secrets", KV_1)
                .get("apps/global/" + secret.name());
        if (features == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Map<String, Object> data = features.getData();
        if (data == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return data;
    }

    enum KnownSecret {
        features,
    }
}
