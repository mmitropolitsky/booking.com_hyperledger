package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.service.FabricClientService;
import org.tudelft.blockchain.booking.otawebapp.service.OrganizationCredentialService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class BookingRepository {

    @Autowired
    FabricClientService fabricClientService;

    @Autowired
    OrganizationCredentialService organizationCredentialService;

    /**
     * Check if date range is bookable.
     *
     * @param orgName
     * @param propertyName
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public boolean isBookable(String orgName, String propertyName, String fromDate, String toDate) throws Exception {
        // QUERY THE BLOCKCHAIN

//        User user = organizationCredentialService.getUser(orgName);
        User user = organizationCredentialService.getCaAdmin(orgName);
        fabricClientService.changeContext(user);

        QueryByChaincodeRequest qpr = getQueryByChaincodeRequest(fromDate, toDate, "isBookable");

        Collection<ProposalResponse> res = fabricClientService.getChannel(propertyName, orgName).queryByChaincode(qpr);
        // display response
        for (ProposalResponse pres : res) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            System.out.println(stringResponse);
            if (pres.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                return true;
            }
            System.out.println(stringResponse);
        }
        return false;
    }

    /**
     * Book dates in the blockchain
     *
     * @param orgName
     * @param propertyName
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public boolean book(String orgName, String propertyName, String fromDate, String toDate) throws Exception {
        User user = organizationCredentialService.getUser(orgName);
        fabricClientService.changeContext(user);

        Channel channel = fabricClientService.getChannel(orgName, propertyName);
        TransactionProposalRequest request = getBookTransactionProposalRequest(fromDate, toDate);

        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());

        // Get transaction proposal responses
        for (ProposalResponse pres : responses) {
            if (pres.getStatus() != ChaincodeResponse.Status.SUCCESS) {
                return false;
            }
        }

        // Try to commit the transaction to the Ordering service
        CompletableFuture<BlockEvent.TransactionEvent> te = channel.sendTransaction(responses);

        // TODO wait for orderer response
        return te != null;
    }

    // TODO clean this up
    private TransactionProposalRequest getBookTransactionProposalRequest(String fromDate, String toDate) throws InvalidArgumentException {
        TransactionProposalRequest request = fabricClientService.getClient().newTransactionProposalRequest();
        ChaincodeID ccid = ChaincodeID.newBuilder().setName("OverbookingChainCode").build();
        request.setChaincodeID(ccid);
        request.setFcn("book");
        String[] arguments = {fromDate, toDate};
        request.setArgs(arguments);
        request.setProposalWaitTime(1000);
        request.setChaincodeLanguage(TransactionRequest.Type.JAVA);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk
        // in transient map
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
        tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
        tm2.put("event", "!".getBytes(UTF_8)); // This should trigger an event see chaincode why.
        request.setTransientMap(tm2);

        return request;
    }

    private QueryByChaincodeRequest getQueryByChaincodeRequest(String fromDate, String toDate, String method) {
        QueryByChaincodeRequest qpr = fabricClientService.getClient().newQueryProposalRequest();
        ChaincodeID overbookingCCID = ChaincodeID.newBuilder().setName("OverbookingChainCode").build();
        qpr.setChaincodeID(overbookingCCID);
        qpr.setFcn(method);
        qpr.setArgs(fromDate, toDate);
        return qpr;
    }

}
