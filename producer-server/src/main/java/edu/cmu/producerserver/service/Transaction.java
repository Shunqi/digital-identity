package edu.cmu.producerserver.service;

import edu.cmu.producerserver.contract.PublicKeys;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

public class Transaction {

    private final static String PRIVATE_KEY = "2cda53167fd7ea1947bf578bcac6a78330e75275c6af090ecef149c98b0356d4";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    private final static String deployedAddress = "0x25e30705f9c7db9a3077adfe05b41db309e6d5bd";

    PublicKeys publicKeys;

    public Transaction() throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        printWeb3Version(web3j);

        // Load Contract
        publicKeys  = loadContract(deployedAddress, web3j,  getCredentialsFromPrivateKey());
        System.out.println("Contract address after loading:  " + publicKeys.getContractAddress());
    }

    private void printWeb3Version(Web3j web3j) {
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String web3ClientVersionString = web3ClientVersion.getWeb3ClientVersion();
        System.out.println("Web3 client version: " + web3ClientVersionString);
    }

    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    private PublicKeys loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return PublicKeys.load(contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    public String getKey(String did) throws Exception {
        return publicKeys.getKey(did).send();
    }
}
