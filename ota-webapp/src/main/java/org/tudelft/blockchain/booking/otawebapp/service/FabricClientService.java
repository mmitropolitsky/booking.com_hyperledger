package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FabricClientService {

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    protected String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.password}")
    protected String adminPassword;

    private HFClient client;

    @PostConstruct
    private void setup() {
        try {
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            // GET HF CLIENT
            client = HFClient.createNewInstance();
            client.setCryptoSuite(cryptoSuite);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public HFClient getClient() {
        return client;
    }

    public void changeContext(User user) throws Exception {
        client.setUserContext(user);
    }
}
