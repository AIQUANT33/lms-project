package com.example.demo.service;

import com.example.demo.contract.Learntrust;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.crypto.Credentials;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

@Service
public class BlockchainService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final Learntrust contract;

    public BlockchainService(
            @Value("${blockchain.rpc.url}") String rpcUrl,
            @Value("${blockchain.private.key}") String privateKey,
            @Value("${blockchain.contract.address}") String contractAddress
    ) {

        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.credentials = Credentials.create(privateKey);

        this.contract = Learntrust.load(
                contractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()
        );
    }

    public MintResponse mintCertificate(
            String studentWallet,
            String certificateHash,
            String tokenURI
    ) throws Exception {

        TransactionReceipt receipt = contract
                .mintCertificate(
                        studentWallet,
                        certificateHash,
                        tokenURI
                )
                .send();

        // Extract event
        List<Learntrust.CertificateMintedEventResponse> events =
                Learntrust.getCertificateMintedEvents(receipt);

        BigInteger tokenId = null;

        if (!events.isEmpty()) {
            tokenId = events.get(0).tokenId;
        }

        return new MintResponse(
                receipt.getTransactionHash(),
                tokenId
        );
    }

    // Inner class to return both txHash and tokenId
    public static class MintResponse {
        private final String transactionHash;
        private final BigInteger tokenId;

        public MintResponse(String transactionHash, BigInteger tokenId) {
            this.transactionHash = transactionHash;
            this.tokenId = tokenId;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public BigInteger getTokenId() {
            return tokenId;
        }
    }
}
