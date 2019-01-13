package org.tudelft.blockchain.booking.client.endpoint;


public class Result {
    private String Credential;
    private String Attrs;
    private String CRI;
    private String Nonce;
    private String CAInfo;
    private String CAName;
    private String CAChain;
    private String Version;


    public Result() {
    }

    public String getCredential() {
        return Credential;
    }

    public void setCredential(String credential) {
        Credential = credential;
    }

    public String getAttrs() {
        return Attrs;
    }

    public void setAttrs(String attrs) {
        Attrs = attrs;
    }

    public String getCRI() {
        return CRI;
    }

    public void setCRI(String CRI) {
        this.CRI = CRI;
    }

    public String getNonce() {
        return Nonce;
    }

    public void setNonce(String nonce) {
        Nonce = nonce;
    }

    public String getCAInfo() {
        return CAInfo;
    }

    public void setCAInfo(String CAInfo) {
        this.CAInfo = CAInfo;
    }

    public String getCAName() {
        return CAName;
    }

    public void setCAName(String CAName) {
        this.CAName = CAName;
    }

    public String getCAChain() {
        return CAChain;
    }

    public void setCAChain(String CAChain) {
        this.CAChain = CAChain;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }
}


