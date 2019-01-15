package org.tudelft.blockchain.booking.otawebapp.repository;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class FabricRepository {

    private HFClient hfClient;

    public HFClient getHfClient() {
        return hfClient;
    }

//    @PostConstruct
    public void setupClient(User userContext) throws CryptoException, InvalidArgumentException, IllegalAccessException,
            InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        // setup the client
        hfClient = HFClient.createNewInstance();
        hfClient.setCryptoSuite(cryptoSuite);
        hfClient.setUserContext(userContext);
    }

    public Collection<ProposalResponse> deployChainCode(String chainCodeName, String codepath, String version, Collection<Peer> peers)
            throws InvalidArgumentException, IOException, ProposalException {
        InstallProposalRequest request = hfClient.newInstallProposalRequest();
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chainCodeName).setVersion(version);
        ChaincodeID chaincodeID = chaincodeIDBuilder.build();

        Logger.getLogger(FabricRepository.class.getName()).log(Level.INFO,
                "Deploying chaincode " + chainCodeName + " using Fabric client " + hfClient.getUserContext().getMspId()
                        + " " + hfClient.getUserContext().getName());

        request.setChaincodeLanguage(TransactionRequest.Type.JAVA);
        request.setChaincodeID(chaincodeID);

        request.setUserContext(hfClient.getUserContext());
//        request.setChaincodeInputStream(new FileInputStream(codepath + File.separator + "src" + File.separator + "Overbooking.java"));
        request.setChaincodeSourceLocation(new File(codepath));
        request.setChaincodeMetaInfLocation(new File(codepath + File.separator +  "manifests"));
//        request.setChaincodeInputStream(Util.generateTarGzInputStream(new File(codepath), "src"));
        request.setChaincodeVersion(version);
        return hfClient.sendInstallProposal(request, peers);
    }






    public Collection<ProposalResponse> instantiateChainCode(Channel channel, String chaincodeName, String version, String chaincodePath,
                                                             String language, String functionName, String[] functionArgs, String policyPath)
            throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException, ExecutionException, InterruptedException {
        Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
                "Instantiate proposal request " + chaincodeName + " on channel " + channel.getName()
                        + " with Fabric client " + hfClient.getUserContext().getMspId() + " "
                        + hfClient.getUserContext().getName());
        InstantiateProposalRequest instantiateProposalRequest = hfClient.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(180000);
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(version).setPath(chaincodePath);
        ChaincodeID ccid = chaincodeIDBuilder.build();
        Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
                "Instantiating Chaincode ID " + chaincodeName + " on channel " + channel.getName());
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

        Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
                "Chaincode " + chaincodeName + " on channel " + channel.getName() + " instantiation " + cf);
        return responses;
    }

}
