package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.repository.PropertyRepository;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.FabricRepository;

import java.time.LocalDate;
import java.util.Collection;

@Component
public class PropertyService {

    private static final String peerName = "peer0.org1.example.com";
    private static final String peerUrl = "grpc://localhost:7051";
    private static final String eventHubName = "eventhub01";
    private static final String eventHubUrl = "grpc://localhost:7053";
    private static final String ordererName = "orderer.example.com";
    private static final String ordererUrl = "grpc://localhost:7050";

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private FabricRepository fabricRepository;

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.affilication}")
    String affiliation;

    @Value("${org.tudelft.blockchain.booking.admin.msp.id}")
    String mspId;

    @Value("${org.tudelft.blockchain.booking.chaincode.path}")
    String chaincodePath;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }


    public Collection<DateAvailabilityPair> getAvailableDates(String propertyId, LocalDate startDate, LocalDate endDate) {
        return propertyRepository.getAvailableDates(propertyId, startDate, endDate);
    }

    public void initProperty(String propertyName) throws Exception {
        HFUser admin = fabricRepository.getAdmin(adminUsername, affiliation, mspId);
        HFClient adminClient = fabricRepository.getAdminClient(admin);
        Channel channel = createPropertyChannel(propertyName, admin, adminClient);
        deployAndInstantiatePropertyChaincode(propertyName, channel);
    }

    private Channel createPropertyChannel(String channelName, HFUser admin, HFClient adminClient) throws Exception {
        return propertyRepository.createPropertyChannel(adminClient, admin, peerName, peerUrl, eventHubName, eventHubUrl, ordererName, ordererUrl, channelName);
    }

    private void deployAndInstantiatePropertyChaincode(String propertyName, Channel channel) {

        String chaincodeName = "Overbooking-" + propertyName;
        try {
            Collection<ProposalResponse> deployProposalResponses =
                    fabricRepository.deployChainCode(chaincodeName, chaincodePath, "1", channel.getPeers());

            String[] args = new String[0];
            Collection<ProposalResponse> instantiationProposalResponses =
                    fabricRepository.instantiateChainCode(channel, chaincodeName, "1",
                            "overbooking", "init", args, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
