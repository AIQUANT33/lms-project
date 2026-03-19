package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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


        //RestTemplate is Spring's built-in HTTP client for making API calls from your backend to other services. 
        RestTemplate restTemplate = new RestTemplate();
        
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("pinata_api_key", apiKey);
        headers.set("pinata_secret_api_key", secretApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON); //tells Pinata the body is JSON

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(metadata, headers); //HttpEntity bundles the body  and headers together into one object — ready to be sent as an HTTP request.
        
        /*
        postForEntity sends the POST request to Pinata and waits for the response. 
        Map.class tells RestTemplate to deserialise the JSON response into a Java Map. 
        ResponseEntity wraps the response body, status code, and headers together.
        */
        ResponseEntity<Map> response =
                restTemplate.postForEntity(PINATA_URL, request, Map.class);



        /*
        
        Pinata's response contains IpfsHash — the Content Identifier (CID).
         A CID is IPFS's way of identifying a file — it's derived from the file's content itself, so the same content always produces the same CID. 
         You build a full HTTP URL from it so anyone can access the metadata in a browser without needing IPFS software.
        
        */
        String cid = (String) response.getBody().get("IpfsHash");

        return "https://gateway.pinata.cloud/ipfs/" + cid;
    }
}


/*
Uploads the certificate metadata (student name, course name, hash, issue date) as a JSON file to IPFS via Pinata's API.
 Returns a permanent public URL  that becomes the NFT's tokenURI

 */