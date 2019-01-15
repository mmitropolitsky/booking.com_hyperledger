package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class BookingRepository extends BaseBlockchainRepository {


    /**
     * Check if date range is bookable.
     *
     * @param fromDate
     * @param toDate
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public boolean isBookable(String fromDate, String toDate) throws ProposalException, InvalidArgumentException {
        // QUERY THE BLOCKCHAIN
        QueryByChaincodeRequest qpr = getQueryByChaincodeRequest(fromDate, toDate, "isBookable");

        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);
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
     * Book dates in the blockchain.
     *
     * @param fromDate
     * @param toDate
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public boolean book(String fromDate, String toDate) throws ProposalException, InvalidArgumentException {

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
        TransactionProposalRequest request = client.newTransactionProposalRequest();
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
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
        ChaincodeID overbookingCCID = ChaincodeID.newBuilder().setName("OverbookingChainCode").build();
        qpr.setChaincodeID(overbookingCCID);
        qpr.setFcn(method);
        qpr.setArgs(fromDate, toDate);
        return qpr;
    }

}
