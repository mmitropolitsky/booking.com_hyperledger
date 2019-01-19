package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.user.IdemixUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.tudelft.blockchain.booking.otawebapp.service.CredentialService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.UUID;

public class BaseBlockchainRepository {

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    protected String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.password}")
    protected String adminPassword;

    @Autowired
    private CredentialService credentialService;

    protected HFClient client;

    protected Channel channel;

    protected IdemixUser user;

    private CryptoSuite cryptoSuite;

    public Collection<Peer> getPeers() {
        return channel.getPeers();
    }

    public Channel getChannel() {
        return channel;
    }

    private boolean isInitialized = false;

    @PostConstruct
    private void init() {
        if (!isInitialized) {
            this.setup();
        }
        isInitialized = true;
    }

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
            user = (IdemixUser) credentialService.getIdemixEnrolledUser(username, userSecret);
            client.setUserContext(user);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void changeToUserContext() throws InvalidArgumentException {
        client.setUserContext(user);
    }

    public void changeToCaAdminContext() throws InvalidArgumentException {
        client.setUserContext(credentialService.getCaAdminUser());
    }

    public void changeToOrgAdminContext() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidArgumentException {
        client.setUserContext(credentialService.getOrgAdminUser());
    }


//
//    public Enrollment getEnrollment() throws Exception {
//        HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", null);
//        caClient.setCryptoSuite(cryptoSuite);
//
//        Enrollment adminEnrollment = caClient.enroll("caAdmin", "adminpw");
//        HFUser caAdmin = new HFUser("caAdmin", "org1", "Org1MSP", adminEnrollment);
//
//        RegistrationRequest registrationRequest = new RegistrationRequest("test", "org1");
//        String secret = caClient.register(registrationRequest, caAdmin);
////        String secret = credentialService.registerUser("test");
//
//
////        Enrollment adminEnrollment = caClient.enroll(adminUsername, adminPassword);
//
//        Enrollment enrollment = caClient.enroll("test", secret);
//        return caClient.idemixEnroll(enrollment, "Org1MSP");
//
//
////        return adminEnrollment;
//    }

}
