package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.UUID;

@Component
public class NetworkService {

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    protected String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.password}")
    protected String adminPassword;

    @Autowired
    private CredentialService credentialService;

    private HFClient client;

    private Channel channel;

    private User user;

    private CryptoSuite cryptoSuite;

    @PostConstruct
    private void setup() {
        try {
            User admin = credentialService.getCaAdminUser();

            cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            // GET HF CLIENT
            client = HFClient.createNewInstance();
            client.setCryptoSuite(cryptoSuite);
            client.setUserContext(admin);

            // CREATE CHANNEL
            Peer peer = client.newPeer("peer0.org1.example.com", "grpc://localhost:7051");
            EventHub eventHub = client.newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = client.newOrderer("orderer.example.com", "grpc://localhost:7050");
            channel = client.newChannel("mychannel");
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();

            String username = UUID.randomUUID().toString();
            String userSecret = credentialService.registerUser(username);

//            user = (IdemixUser) credentialService.getIdemixEnrolledUser(username, userSecret);
            user = credentialService.getEnrolledUser(username, userSecret);
            client.setUserContext(user);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setDefaultContext() throws Exception {
        this.changeToUserContext();
    }

    public void changeToUserContext() throws Exception {
        client.setUserContext(user);
    }

    public void changeToCaAdminContext() throws Exception {
        client.setUserContext(credentialService.getCaAdminUser());
    }

    public void changeToOrgAdminContext() throws Exception {
        client.setUserContext(credentialService.getOrgAdminUser());
    }

    public HFClient getClient() {
        return client;
    }

    public Collection<Peer> getPeers() {
        return channel.getPeers();
    }

    public Channel getChannel() {
        return channel;
    }


}
