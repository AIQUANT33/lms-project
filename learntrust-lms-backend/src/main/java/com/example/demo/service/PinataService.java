package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PinataService {

    @Value("${pinata.api.key}")
    private String apiKey;

    @Value("${pinata.secret.api.key}")
    private String secretApiKey;

    private final String PINATA_URL =
            "https://api.pinata.cloud/pinning/pinJSONToIPFS";

    public String uploadMetadata(Map<String, Object> metadata) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("pinata_api_key", apiKey);
        headers.set("pinata_secret_api_key", secretApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(metadata, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(PINATA_URL, request, Map.class);

        String cid = (String) response.getBody().get("IpfsHash");

        return "https://gateway.pinata.cloud/ipfs/" + cid;
    }
}
