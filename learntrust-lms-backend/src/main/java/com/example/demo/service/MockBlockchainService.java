package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockBlockchainService {

    /*
     * This simulates sending certificate hash to Ethereum
     * Returns fake transaction hash
     */
    public String storeHashOnBlockchain(String certificateHash) {

        String fakeTxHash = "0xMOCK_" +
                UUID.randomUUID().toString().replace("-", "");

        System.out.println("Hash sent to blockchain: " + certificateHash);
        System.out.println("Transaction hash: " + fakeTxHash);

        return fakeTxHash;
    }
}
