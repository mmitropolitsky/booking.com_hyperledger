package org.tudelft.blockchain.booking.ca.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CAInfo implements Serializable {

    @JsonProperty("CAName")
    private String caName;

    @JsonProperty("CAChain")
    private String caChain;

    @JsonProperty("IssuerPublicKey")
    private String issuerPublicKey;

    @JsonProperty("IssuerRevocationPublicKey")
    private String issuerRevocationPublicKey;

    @JsonProperty("Version")
    private String version;

    public String getCaName() {
        return caName;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

    public String getCaChain() {
        return caChain;
    }

    public void setCaChain(String caChain) {
        this.caChain = caChain;
    }

    public String getIssuerPublicKey() {
        return issuerPublicKey;
    }

    public void setIssuerPublicKey(String issuerPublicKey) {
        this.issuerPublicKey = issuerPublicKey;
    }

    public String getIssuerRevocationPublicKey() {
        return issuerRevocationPublicKey;
    }

    public void setIssuerRevocationPublicKey(String issuerRevocationPublicKey) {
        this.issuerRevocationPublicKey = issuerRevocationPublicKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
