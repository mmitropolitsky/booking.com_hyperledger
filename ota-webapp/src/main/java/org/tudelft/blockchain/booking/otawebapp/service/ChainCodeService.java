package org.tudelft.blockchain.booking.otawebapp.service;

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

    public void deployAndInstantiateChainCode() throws Exception {
        Collection<ProposalResponse> deployProposalResponses =
                fabricRepository.deployChainCode("OverbookingChainCode", chaincodePath, "1", fabricRepository.getPeers());

        for (ProposalResponse res : deployProposalResponses) {
            Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
                    "Overbooking" + "- Chain code deployment " + res.getStatus());
        }

        String[] args = new String[0];
        Collection<ProposalResponse> instantiationProposalResponses =
                fabricRepository.instantiateChainCode(fabricRepository.getChannel(), "OverbookingChainCode", "1",
                        "overbooking", "init", args, "./out/production/resources/policy.yml");

        for (ProposalResponse res : instantiationProposalResponses) {
            Logger.getLogger(ChainCodeService.class.getName()).log(Level.INFO,
                    "Overbooking" + "- Chain code instantiation " + res.getStatus());
        }

    }

}
