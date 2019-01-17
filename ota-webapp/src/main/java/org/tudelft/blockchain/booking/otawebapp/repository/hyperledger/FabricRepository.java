package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class FabricRepository extends BaseBlockchainRepository {

    @Value("${org.tudelft.blockchain.booking.admin.private.key.path}")
    String adminPrivateKeyPath;

    @Value("${org.tudelft.blockchain.booking.admin.certificate.path}")
    String adminCertificatePath;

    @Override
    public Enrollment getEnrollment() throws Exception {
        File pkFolder = new File(adminPrivateKeyPath);
        File[] pkFiles = pkFolder.listFiles();

        File certFolder = new File(adminCertificatePath);
        File[] certFiles = certFolder.listFiles();

        return Util.getEnrollment(pkFolder.getPath(), pkFiles[0].getName(), certFolder.getPath(), certFiles[0].getName());
    }

    public Collection<ProposalResponse> deployChainCode(String chainCodeName, String codepath, String version, Collection<Peer> peers)
            throws InvalidArgumentException, ProposalException {


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
    }


    public Collection<ProposalResponse> instantiateChainCode(Channel channel, String chaincodeName, String version, String chaincodePath,
                                                             String functionName, String[] functionArgs, String policyPath)
            throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException, ExecutionException, InterruptedException {

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

        if (policyPath != null) {
            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
            chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
            instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest);
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        cf.get();

        Logger.getLogger(FabricRepository.class.getName()).log(Level.INFO,
                "Chaincode " + chaincodeName + " on channel " + channel.getName() + " instantiation " + cf);
        return responses;
    }

}