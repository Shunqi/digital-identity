package edu.cmu.producerserver.service;

import edu.cmu.producerserver.contract.PublicKeys;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;

import java.io.IOException;
import java.math.BigInteger;

public class Transaction {

    private final static String PRIVATE_KEY = "1f15d92bbaa04b989e584d0f0df559582acaa7656f6fae90e52658e6410ab038";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final static String RECIPIENT = "0x1050BC98DEb8C92167A97E11209AAAd4895fB5D1";
    private final static String CONTRACT_ADDRESS = "0x080136cfb266806b22d65db843d1105b75abea3b";

    PublicKeys publicKeys;

    public Transaction() throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        printWeb3Version(web3j);

        // Deploy contract
        String deployedAddress = deployContract(web3j,  getCredentialsFromPrivateKey());
        System.out.println("Deployed Address:  " + deployedAddress);

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

    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils.loadCredentials(
                "passphrase",
                "wallet/path"
        );
    }

    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
        return PublicKeys.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT)
                .send()
                .getContractAddress();
    }

    private PublicKeys loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return PublicKeys.load(contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    public void setKey(String did, String publicKey) throws Exception {
        publicKeys.setKey(did, publicKey).send();
    }

    public String getKey(String did) throws Exception {
        return publicKeys.getKey(did).send();
    }
}
