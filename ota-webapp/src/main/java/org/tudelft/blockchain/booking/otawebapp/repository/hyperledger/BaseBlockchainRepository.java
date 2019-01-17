package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Value;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;

import javax.annotation.PostConstruct;
import java.util.Collection;

public class BaseBlockchainRepository {

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    protected String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.password}")
    protected String adminPassword;

    protected HFClient client;

    protected Channel channel;

    private CryptoSuite cryptoSuite;

    public Collection<Peer> getPeers() {
        return channel.getPeers();
    }

    public Channel getChannel() {
        return channel;
    }

    @PostConstruct
    private void setup() {
        try {
            cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
            Enrollment adminEnrollment = getEnrollment();
            HFUser admin = new HFUser(adminUsername, "org1", "Org1MSP", adminEnrollment);

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Enrollment getEnrollment() throws Exception {
        HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", null);
        caClient.setCryptoSuite(cryptoSuite);
        Enrollment adminEnrollment = caClient.enroll(adminUsername, adminPassword);

        return adminEnrollment;
    }

}
