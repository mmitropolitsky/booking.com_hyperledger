package org.tudelft.blockchain.booking.otawebapp.util;

public class OrgStringBuilder {
    public static String getDomainName(String orgName) {
        // turn OtaA to ota-a

        return orgName.replaceAll("([^_A-Z])([A-Z])", "$1-$2").toLowerCase() + ".tudelft.org";
    }

    public static String getPeerName(String orgName, int peerNumber) {
        return "peer" + peerNumber + "." + getDomainName(orgName);
    }

    public static String getPeerUrl() {
        return "grpc://localhost:7051";
    }

    public static String getEventHubName() {
        return "eventhub01";
    }

    public static String getEventHubUrl() {
        return "grpc://localhost:7053";
    }

    public static String getOrdererName() {
        return "orderer.example.com";
    }

    public static String getOrdererUrl() {
        return "grpc://localhost:7050";
    }

    public static String getCaUrl() {
        return "http://localhost:7054";
    }

    public static String getMspId(String orgName) {
        return orgName + "MSP";
    }
}
