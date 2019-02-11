package org.tudelft.blockchain.booking.otawebapp.service.hyperledger;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.FabricRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ChainCodeService {

    private final FabricRepository fabricRepository;
    public static String CHAINCODE_NAME = "overbooking";
    private static Map<String, Boolean> peersInstalledChaincode = new HashMap<>();

    @Value("${org.tudelft.blockchain.booking.chaincode.path}")
    String chaincodePath;

    @Autowired
    public ChainCodeService(FabricRepository fabricRepository) {
        this.fabricRepository = fabricRepository;
    }


    public String installOverbookingChainCode(String orgName, Collection<Peer> peers) throws Exception {
        if (peersInstalledChaincode.get(orgName + CHAINCODE_NAME) == null) {
            Collection<ProposalResponse> deployProposalResponses =
                    fabricRepository.deployChainCode(orgName, CHAINCODE_NAME, chaincodePath, "1", peers);

            for (ProposalResponse res : deployProposalResponses) {
                Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
                        "Overbooking" + "- Chain code deployment " + res.getStatus());
            }
            peersInstalledChaincode.put(orgName + CHAINCODE_NAME, true);
        }


        return CHAINCODE_NAME;
    }

    // TODO change return type?
    public void instantiateChainCode(String orgName, String chaincodeName, Channel channel, String[] args) throws Exception {
        Collection<ProposalResponse> instantiationProposalResponses =
                fabricRepository.instantiateChainCode(orgName, channel, chaincodeName, "1",
                        "overbooking", "init", args, null);//"./out/production/resources/policy.yml");

        for (ProposalResponse res : instantiationProposalResponses) {
            Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
                    "Overbooking" + "- Chain code instantiation " + res.getStatus());
        }
    }


}
