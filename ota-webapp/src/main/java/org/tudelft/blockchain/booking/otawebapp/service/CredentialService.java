package org.tudelft.blockchain.booking.otawebapp.service;

import org.apache.milagro.amcl.FP256BN.BIG;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.idemix.IdemixCredential;
import org.hyperledger.fabric.sdk.idemix.IdemixPseudonym;
import org.hyperledger.fabric.sdk.idemix.IdemixSignature;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Component
public class CredentialService {

    private static final String USERNAME = "admin2";
    private static final String PASSWORD = "xsTdJWcnDydn";
    private static final String LOCALHOST = "http://localhost";
    private static final String PORT = ":7054";
    private static final String CREDENTIAL_ENDPOINT = "/api/v1/idemix/credential";

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.private.key.path}")
    String adminPrivateKeyPath;

    @Value("${org.tudelft.blockchain.booking.admin.certificate.path}")
    String adminCertificatePath;

//    @Autowired
//    RestTemplate restTemplate;

//    public NonceResponse getNonceResponse() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.setBasicAuth(USERNAME, PASSWORD);
//        HttpEntity<String> entity = new HttpEntity<String>("{}", headers);
//
//        return restTemplate.exchange(LOCALHOST + PORT + CREDENTIAL_ENDPOINT, HttpMethod.POST, entity, NonceResponse.class).getBody();
//    }

    /**
     * Get an IdemixEnrollment for an already enrolled HFUser (with a regular x509 Enrollment)
     *
     * @param user: existing and enrolled user
     * @param caURL: URL to connect to the Certificate Authority server
     * @return an IdemixEnrollment for the given user, issued by the specified CA
     * @throws MalformedURLException
     * @throws EnrollmentException
     * @throws InvalidArgumentException
     */
    public IdemixEnrollment idemixEnroll(HFUser user, String caURL)
            throws MalformedURLException, EnrollmentException, InvalidArgumentException,
            IllegalAccessException, InvocationTargetException,
            org.hyperledger.fabric.sdk.exception.InvalidArgumentException,
            InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {

        HFCAClient hfcaClient = HFCAClient.createNewInstance(caURL, null);
        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        Enrollment enrollment = hfcaClient.idemixEnroll(user.getEnrollment(), user.getMspId());
        return (IdemixEnrollment) enrollment;
    }

    public HFUser adminUser(String caURL)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File pkFolder = new File(adminPrivateKeyPath);
        File[] pkFiles = pkFolder.listFiles();
        File certFolder = new File(adminCertificatePath);
        File[] certFiles = certFolder.listFiles();
        Enrollment adminEnrollment =
                org.tudelft.blockchain.booking.otawebapp.util.Util.getEnrollment
                        (pkFolder.getPath(), pkFiles[0].getName(), certFolder.getPath(), certFiles[0].getName());
        return new HFUser(adminUsername, "org1.example.com", "Org1MSP", adminEnrollment);
    }

    /**
     * Create an Idemix Pseudonym for an existing IdemixEnrollment Credential
     * @param idemixEnrollment
     * @return
     */
    public IdemixPseudonym idemixPseudonym(IdemixEnrollment idemixEnrollment) {

        return new IdemixPseudonym(BIG.fromBytes(idemixEnrollment.getKey().getEncoded()),
                idemixEnrollment.getIpk());
    }

    /**
     * Sign a given message with a Zero-Knowledge Proof of the signature for an enrolled user's specific Pseudonym
     * @param idemixEnrollment: existing IdemixEnrollment for a user
     * @param idemixPseudonym: existing IdemixPseudonym to sign as
     * @param message: message to sign
     * @return
     */
    public IdemixSignature idemixSignature(IdemixEnrollment idemixEnrollment,
                                           IdemixPseudonym idemixPseudonym, byte[] message) {

        IdemixCredential cred = idemixEnrollment.getCred();
        boolean[] disclosure = new boolean[cred.getAttrs().length];
        // disclose all attributes
        // TODO figure out which attributes to disclose
        for (int i = 0 ; i < disclosure.length; i++)
            disclosure[i] = true;

        return new IdemixSignature(idemixEnrollment.getCred(),
                BIG.fromBytes(idemixEnrollment.getKey().getEncoded()),
                idemixPseudonym,
                idemixEnrollment.getIpk(),
                disclosure,
                message,
                0,
                idemixEnrollment.getCri());
    }

    /**
     * Sign a given message with a Zero-Knowledge Proof of the signature for an enrolled user
     * using a volatile Pseudonym (is not stored, cannot be re-used)
     * @param idemixEnrollment
     * @param message
     * @return
     */
    public IdemixSignature idemixSignature(IdemixEnrollment idemixEnrollment, byte[] message) {
        return idemixSignature(idemixEnrollment, idemixPseudonym(idemixEnrollment), message);
    }
}
