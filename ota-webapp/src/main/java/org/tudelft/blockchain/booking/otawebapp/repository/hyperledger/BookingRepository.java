package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.service.FabricClientService;
import org.tudelft.blockchain.booking.otawebapp.service.OrganizationCredentialService;
import org.tudelft.blockchain.booking.otawebapp.service.hyperledger.ChannelService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.tudelft.blockchain.booking.otawebapp.service.hyperledger.ChainCodeService.CHAINCODE_NAME;

@Component
public class BookingRepository {

    public static String IS_BOOKABLE_FUNCTION_NAME = "isBookable";
    public static String BOOK_FUNCTION_NAME = "book";
    public static long PROPOSAL_WAIT_TIME = 180000;

    private final FabricClientService fabricClientService;

    private final OrganizationCredentialService organizationCredentialService;

    private final ChannelService channelService;

    @Autowired
    public BookingRepository(FabricClientService fabricClientService,
                             OrganizationCredentialService organizationCredentialService, ChannelService channelService) {
        this.fabricClientService = fabricClientService;
        this.organizationCredentialService = organizationCredentialService;
        this.channelService = channelService;
    }


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
//        User user = organizationCredentialService.getOrgAdmin(orgName);

        Channel channel = channelService.getChannel(orgName, propertyName);
        User user = organizationCredentialService.getUser(orgName);
        fabricClientService.changeContext(user);

        QueryByChaincodeRequest qpr = getQueryByChaincodeRequest(fromDate, toDate, IS_BOOKABLE_FUNCTION_NAME);

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
//        User user = organizationCredentialService.getOrgAdmin(orgName);
        Channel channel = channelService.getChannel(orgName, propertyName);
        TransactionProposalRequest request = getBookTransactionProposalRequest(fromDate, toDate);
        User user = organizationCredentialService.getUser(orgName);
        fabricClientService.changeContext(user);


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

    private TransactionProposalRequest getBookTransactionProposalRequest(String fromDate, String toDate) throws InvalidArgumentException {
        TransactionProposalRequest request = fabricClientService.getClient().newTransactionProposalRequest();
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(CHAINCODE_NAME).build();
        request.setChaincodeID(ccid);
        request.setFcn(BOOK_FUNCTION_NAME);
        String[] arguments = {fromDate, toDate};
        request.setArgs(arguments);
        request.setProposalWaitTime(PROPOSAL_WAIT_TIME);
        request.setChaincodeLanguage(TransactionRequest.Type.JAVA);
        request.setTransientMap(prepareTransientMap());

        return request;
    }

    // Necessary data to be able to use chaincode functionality
    private Map<String, byte[]> prepareTransientMap() {
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        // in transient map
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm.put("result", ":)".getBytes(UTF_8));
        tm.put("event", "!".getBytes(UTF_8));
        return tm;
    }

    private QueryByChaincodeRequest getQueryByChaincodeRequest(String fromDate, String toDate, String method) {
        QueryByChaincodeRequest qpr = fabricClientService.getClient().newQueryProposalRequest();
        ChaincodeID overbookingCCID = ChaincodeID.newBuilder().setName(CHAINCODE_NAME).build();
        qpr.setChaincodeLanguage(TransactionRequest.Type.JAVA);
        qpr.setChaincodeID(overbookingCCID);
        qpr.setFcn(method);
        qpr.setArgs(fromDate, toDate);
        return qpr;
    }

}
