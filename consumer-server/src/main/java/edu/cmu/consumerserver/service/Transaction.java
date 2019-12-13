package edu.cmu.consumerserver.service;

import edu.cmu.consumerserver.contract.PublicKeys;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

public class Transaction {

    //private final static String PRIVATE_KEY = "d55bb34f43683cc93dec1ef57605988e07d8f913626c9ce61a26b5b10d628b1a";
    private final static String PRIVATE_KEY = "54c1bf5ad1242e728a89ff24e6b9253bc47acd9c18db06531bed3ccc99bf968c";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    private final static String deployedAddress = "0xddea124794659ce557c53f1760e8923a11c4b50e";

    PublicKeys publicKeys;

    public Transaction() throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        printWeb3Version(web3j);//
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