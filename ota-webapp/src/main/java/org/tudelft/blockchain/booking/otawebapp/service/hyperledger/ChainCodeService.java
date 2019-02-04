package org.tudelft.blockchain.booking.otawebapp.service.hyperledger;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.FabricRepository;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ChainCodeService {

    @Autowired
    FabricRepository fabricRepository;

    @Value("${org.tudelft.blockchain.booking.chaincode.path}")
    String chaincodePath;


    public String installOverbookingChainCode(String orgName, String propertyName, Channel channel) throws Exception {
        String chainCodeName = propertyName + "Overbooking";
        Collection<ProposalResponse> deployProposalResponses =
                fabricRepository.deployChainCode(orgName, chainCodeName, chaincodePath, "1", channel.getPeers());

        for (ProposalResponse res : deployProposalResponses) {
            Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
                    "Overbooking" + "- Chain code deployment " + res.getStatus());
        }

        return chainCodeName;
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
