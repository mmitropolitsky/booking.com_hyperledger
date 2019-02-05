package org.tudelft.blockchain.booking.otawebapp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrgStringBuilder {

    /**
     * Peer URLs
     */
    @Value("${org.tudelft.blockchain.booking.po.peer.port}")
    private int peerPortPropertyOwner;

    @Value("${org.tudelft.blockchain.booking.ota.a.peer.port}")
    private int peerPortOtaA;

    @Value("${org.tudelft.blockchain.booking.ota.b.peer.port}")
    private int peerPortOtaB;

    /**
     * Event hubs
     */
    @Value("${org.tudelft.blockchain.booking.po.eventhub.port}")
    private int eventHubPortPropertyOwner;

    @Value("${org.tudelft.blockchain.booking.ota.a.eventhub.port}")
    private int eventHubPortOtaA;

    @Value("${org.tudelft.blockchain.booking.ota.b.eventhub.port}")
    private int eventHubPortOtaB;


    public String getDomainName(String orgName) {
        // turn OtaA to ota-a.tudelft.org
        return orgName.replaceAll("([^_A-Z])([A-Z])", "$1-$2").toLowerCase() + ".tudelft.org";
    }

    public String getPeerName(String orgName, int peerNumber) {
        // turn OtaA, 0 to peer0.ota-a.org.tudelft.org
        return "peer" + peerNumber + "." + getDomainName(orgName);
    }

    public String getPeerUrl(String orgName) {
        int port;
        switch (orgName) {
            case "OtaA":
                port = peerPortOtaA;
                break;
            case "OtaB":
                port = peerPortOtaB;
                break;
            case "PropertyOwner":
            default:
                port = peerPortPropertyOwner;
                break;

        }
        return "grpc://localhost:" + port;//o"7051";
    }

    public String getEventHubName() {
        return "eventhub01";
    }

    public String getEventHubUrl(String orgName) {
        int port;
        switch (orgName) {
            case "OtaA":
                port = eventHubPortOtaA;
                break;
            case "OtaB":
                port = eventHubPortOtaB;
                break;
            case "PropertyOwner":
            default:
                port = eventHubPortPropertyOwner;
                break;

        }
        return "grpc://localhost:" + port;//o"7051";
    }

    public String getOrdererName() {
        return "orderer.example.com";
    }

    public String getOrdererUrl() {
        return "grpc://localhost:7050";
    }

    public String getCaUrl(String orgName) {
        int port = 7054;
        if ("OtaA".equals(orgName)) {
            port = 8054;
        }
        return "http://localhost:" + port;
    }

    public String getMspId(String orgName) {
        return orgName + "MSP";
    }
}
