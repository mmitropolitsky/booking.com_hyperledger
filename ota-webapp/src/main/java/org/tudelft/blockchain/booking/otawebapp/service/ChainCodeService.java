package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.helper.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.repository.FabricRepository;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ChainCodeService {

    @Autowired
    FabricRepository fabricRepository;


    public void createChannel() throws Exception {

//        // GET HF CA CLIENT
//        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
//        HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", null);
//        caClient.setCryptoSuite(cryptoSuite);
////        HFCAClient hfcaClient = getHfCaClient("localhost:7054", null);
//
////        ENROLL USER
//        Enrollment adminEnrollment = caClient.enroll("admin", "adminpw");
//        HFUser admin = new HFUser("admin", "org1", "Org1MSP", adminEnrollment);


// GET HF CLIENT
//        HFClient client = HFClient.createNewInstance();
//        client.setCryptoSuite(cryptoSuite);
//        client.setUserContext(admin);

        // CREATE CHANNEL
//        Peer peer = client.newPeer("peer0.org1.example.com", "grpc://localhost:7051");
//        // eventhub name and endpoint in fabcar network
//        EventHub eventHub = client.newEventHub("eventhub01", "grpc://localhost:7053");
//        // orderer name and endpoint in fabcar network
//        Orderer orderer = client.newOrderer("orderer.example.com", "grpc://localhost:7050");
//        // channel name in fabcar network
//        Channel channel = client.newChannel("mychannel");
//        channel.addPeer(peer);
//        channel.addEventHub(eventHub);
//        channel.addOrderer(orderer);
//        channel.initialize();


        // QUERY THE BLOCKCHAIN
//        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
//        ChaincodeID fabcarCCID = ChaincodeID.newBuilder().setName("fabcar").build();
//        qpr.setChaincodeID(fabcarCCID);
//        qpr.setFcn("queryAllCars");

//        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);
        // display response
//        for (ProposalResponse pres : res) {
//            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
//            System.out.println(stringResponse);
//        }


    }

    public void installChainCode() throws InvalidArgumentException, TransactionException,
            IOException, ProposalException, IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException,
            EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, ChaincodeEndorsementPolicyParseException, ExecutionException, InterruptedException {

        String chaincodePath = "/home/milko/Projects/blockchain_booking/overbooking/src/main/java/org/tudelft/blockchain/booking/Overbooking.java";

//        HFClient client = fabricRepository.getHfClient();

        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", null);
        caClient.setCryptoSuite(cryptoSuite);

//        ENROLL USER
        Enrollment adminEnrollment = caClient.enroll("admin", "adminpw");
        HFUser admin1 = new HFUser("admin", "org1.example.com", "Org1MSP", adminEnrollment);
//        adminEnrollment.

//        Enrollment admin2Enrollment = caClient.enroll("admin", "adminpw");
//        HFUser admin2 = new HFUser("admin", "org2", "Org2MSP", adminEnrollment);

        fabricRepository.setupClient(admin1);
        HFClient client = fabricRepository.getHfClient();


        Peer peer10 = client.newPeer("peer0.org1.example.com", "grpc://localhost:7051");
//        Peer peer11 = client.newPeer("peer1.org1.example.com", "grpc://localhost:7051");
//        Peer peer20 = client.newPeer("peer0.org2.example.com", "grpc://localhost:7051");
//        Peer peer21 = client.newPeer("peer1.org2.example.com", "grpc://localhost:7051");


        // eventhub name and endpoint in fabcar network
        EventHub eventHub = client.newEventHub("eventhub01", "grpc://localhost:7053");
        // orderer name and endpoint in fabcar network
        Orderer orderer = client.newOrderer("orderer.example.com", "grpc://localhost:7050");
        // channel name in fabcar network
        Channel channel = client.newChannel("mychannel");
        channel.addPeer(peer10);
//        channel.addPeer(peer11);
//        channel.addPeer(peer20);
//        channel.addPeer(peer21);
        channel.addEventHub(eventHub);
        channel.addOrderer(orderer);
        channel.initialize();

        List<Peer> org1 = Arrays.asList(peer10); //, peer11);
//        List<Peer> org2 = Arrays.asList(peer20, peer21);


        Collection<ProposalResponse> deployProposalResponses = fabricRepository.deployChainCode("Overbooking",
                "chaincode", "1", org1);

        for (ProposalResponse res : deployProposalResponses) {
            Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
                    "Overbooking" + "- Chain code deployment " + res.getStatus());
        }

//        client.setUserContext(admin2);

//        Collection<ProposalResponse> deployProposal2Responses = fabricRepository.deployChainCode("Overbooking",
//                "/home/milko/Projects/blockchain_booking/overbooking", "main/java/org/tudelft/blockchain/booking",
//                TransactionRequest.Type.JAVA.toString(), "1", org2);

//        for (ProposalResponse res : deployProposal2Responses) {
//            Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
//                    "Overbooking" + "- Chain code deployment " + res.getStatus());
//        }

        String[] args = {""};
        deployProposalResponses = fabricRepository.instantiateChainCode(channel, "Overbooking", "1",
                "overbooking",
                TransactionRequest.Type.JAVA.toString(), "init", args, null);

        for (ProposalResponse res : deployProposalResponses) {
            Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
                    "Overbooking" + "- Chain code instantiation " + res.getStatus());
        }

    }

    HFCAClient getHfCaClient(String caUrl, Properties caClientProperties) throws Exception {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFCAClient caClient = HFCAClient.createNewInstance(caUrl, caClientProperties);
        caClient.setCryptoSuite(cryptoSuite);
        return caClient;
    }

    public static void cleanUp() {
        String directoryPath = "users";
        File directory = new File(directoryPath);
        deleteDirectory(directory);
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }

        // either file or an empty directory
        Logger.getLogger(Util.class.getName()).log(Level.INFO, "Deleting - " + dir.getName());
        return dir.delete();
    }
}
