package org.tudelft.blockchain.booking.ca.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Result implements Serializable {
    @JsonProperty("Credential")
    private String credential;

    @JsonProperty("Attrs")
    private String attrs;

    @JsonProperty("CRI")
    private String cri;

    @JsonProperty("Nonce")
    private String nonce;

    @JsonProperty("CAInfo")
    private CAInfo caInfo;

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public String getCri() {
        return cri;
    }

    public void setCri(String cri) {
        this.cri = cri;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public CAInfo getCaInfo() {
        return caInfo;
    }

    public void setCaInfo(CAInfo caInfo) {
        this.caInfo = caInfo;
    }
}


