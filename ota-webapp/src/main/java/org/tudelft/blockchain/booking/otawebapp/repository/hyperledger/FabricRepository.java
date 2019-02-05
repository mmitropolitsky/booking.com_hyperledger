package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.service.FabricClientService;
import org.tudelft.blockchain.booking.otawebapp.service.OrganizationCredentialService;
import org.tudelft.blockchain.booking.otawebapp.service.hyperledger.ChannelService;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class FabricRepository {

    @Autowired
    FabricClientService fabricClientService;

    @Autowired
    ChannelService channelConfigurationService;

    @Autowired
    OrganizationCredentialService organizationCredentialService;




    public Collection<ProposalResponse> deployChainCode(String orgName, String chainCodeName, String codepath, String version, Collection<Peer> peers)
            throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chainCodeName).setVersion(version);
        ChaincodeID chaincodeID = chaincodeIDBuilder.build();

        Logger.getLogger(FabricRepository.class.getName()).log(Level.INFO,
                "Deploying chaincode " + chainCodeName + " using Fabric client " + client.getUserContext().getMspId()
                        + " " + client.getUserContext().getName());

        // Build Install Proposal Request
        InstallProposalRequest request = client.newInstallProposalRequest();

        request.setChaincodeLanguage(TransactionRequest.Type.JAVA);
        request.setChaincodeID(chaincodeID);
        request.setUserContext(client.getUserContext());
        request.setChaincodeSourceLocation(new File(codepath));
        request.setChaincodeMetaInfLocation(new File(codepath + File.separator + "manifests"));
        request.setChaincodeVersion(version);

        return client.sendInstallProposal(request, peers);
//        fabricClientService.changeToUserContext();
    }

    public Collection<ProposalResponse> instantiateChainCode(String orgName, Channel channel, String chaincodeName, String version, String chaincodePath,
                                                             String functionName, String[] functionArgs, String policyPath)
            throws Exception {

        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();


        Logger.getLogger(FabricRepository.class.getName()).log(Level.INFO,
                "Instantiate proposal request " + chaincodeName + " on channel " + channel.getName()
                        + " with Fabric client " + client.getUserContext().getMspId() + " "
                        + client.getUserContext().getName());


        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(version).setPath(chaincodePath);
        ChaincodeID ccid = chaincodeIDBuilder.build();
        Logger.getLogger(FabricRepository.class.getName()).log(Level.INFO,
                "Instantiating Chaincode ID " + chaincodeName + " on channel " + channel.getName());

        // Send instantiation proposal request
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(180000);
        instantiateProposalRequest.setChaincodeID(ccid);
        instantiateProposalRequest.setChaincodeLanguage(TransactionRequest.Type.JAVA);
        instantiateProposalRequest.setFcn(functionName);
        instantiateProposalRequest.setArgs(functionArgs);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);

//        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
//        if (policyPath != null) {
//            chaincodeEndorsementPolicy.fromFile(new File(policyPath));
//            instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
//        }

        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest);
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
//        fabricClientService.changeToUserContext();

//        cf.get();

        Logger.getLogger(FabricRepository.class.getName()).log(Level.INFO,
                "Chaincode " + chaincodeName + " on channel " + channel.getName() + " instantiation " + cf);

        return responses;
    }

}
