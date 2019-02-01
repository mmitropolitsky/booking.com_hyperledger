package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.util.OrgStringBuilder;

import javax.annotation.PostConstruct;

@Component
public class FabricClientService {

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    protected String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.password}")
    protected String adminPassword;

    private HFClient client;


    private User user;

    private CryptoSuite cryptoSuite;

    @PostConstruct
    private void setup() {
        try {
//            User admin = credentialService.getCaAdminUser();

            cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            // GET HF CLIENT
            client = HFClient.createNewInstance();
            client.setCryptoSuite(cryptoSuite);
//            client.setUserContext(admin);

            // CREATE CHANNEL
//            Peer peer = client.newPeer("peer0.org1.example.com", "grpc://localhost:7051");
//            EventHub eventHub = client.newEventHub("eventhub01", "grpc://localhost:7053");
//            Orderer orderer = client.newOrderer("orderer.example.com", "grpc://localhost:7050");
//            channel = client.newChannel("mychannel");
//            channel.addPeer(peer);
//            channel.addEventHub(eventHub);
//            channel.addOrderer(orderer);
//            channel.initialize();

//            String username = UUID.randomUUID().toString();
//            String userSecret = credentialService.registerUser(username);

//            user = (IdemixUser) credentialService.getIdemixEnrolledUser(username, userSecret);
//            user = credentialService.getEnrolledUser(username, userSecret);
//            client.setUserContext(user);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public HFClient getClient() {
        return client;
    }

    public Channel getChannel(String channelName, String orgName) throws Exception {
        Channel channel = client.getChannel(channelName);
        if (channel == null) {
            String peerName = OrgStringBuilder.getPeerName(orgName, 0);
            Peer peer = client.newPeer(peerName, "grpc://localhost:7051");
            EventHub eventHub = client.newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = client.newOrderer("orderer.example.com", "grpc://localhost:7050");

            channel = client.newChannel(channelName);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();
        }
        return channel;
    }


    public void changeContext(User user) throws Exception {
        client.setUserContext(user);
    }


    public void changeToUserContext() throws Exception {
        client.setUserContext(user);
    }


}
