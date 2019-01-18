package org.tudelft.blockchain.booking.otawebapp.service;

import org.apache.milagro.amcl.FP256BN.BIG;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.idemix.IdemixCredential;
import org.hyperledger.fabric.sdk.idemix.IdemixPseudonym;
import org.hyperledger.fabric.sdk.idemix.IdemixSignature;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.user.IdemixUser;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.CredentialRepository;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.UserRepository;

import javax.annotation.PostConstruct;
import java.util.IdentityHashMap;
import java.util.Properties;

@Component
public class CredentialService {

    @Autowired
    UserRepository userRepository;

    @Value("${ADMIN_USERNAME}")
    protected String adminUsername;

    @Value("${ADMIN_PASSWORD}")
    protected String adminPassword;

    @Value("${CA_URL}")
    protected String caURL;

    @Value("${ORG_NAME}")
    protected String orgName;

    @Value("${MSP_ID}")
    protected String mspID;

    protected HFCAClient hfcaClient;

    protected HFUser admin;

//    @Autowired
//    CredentialRepository credentialRepository;

    @PostConstruct
    public void setUp() {
        try {
            // cf Fabric Java SDK source code for HFCAClient for documentation on supported Properties
            Properties properties = new Properties();
            properties.setProperty("allowAllHostnames", "true");

            hfcaClient = HFCAClient.createNewInstance(caURL, properties);
            // Set crypto primitives to the default crypto primitives returned by the factory
            hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

            Enrollment adminEnrollment = hfcaClient.enroll(adminUsername, adminPassword);
            admin = new HFUser(adminUsername, orgName, mspID, adminEnrollment);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a new User for a given organization
     * @param username
     * @return the User Secret generated i.e. password
     * @throws Exception
     */
    public String registerUser(String username)
            throws RegistrationException, InvalidArgumentException {
        try {
            RegistrationRequest registrationRequest = new RegistrationRequest(username, orgName);
            String secret = hfcaClient.register(registrationRequest, admin);
            return secret;
        } catch (RegistrationException | InvalidArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Enroll a registered User
     * @param username
     * @param userSecret
     * @return
     * @throws Exception
     */
    public User enrollUser(String username, String userSecret)
            throws EnrollmentException, InvalidArgumentException {
        Enrollment enrollment = hfcaClient.enroll(username, userSecret);
        HFUser user = new HFUser(username, orgName, mspID, enrollment);
        userRepository.putUser(user);

        return user;
    }

    public User getUser(String userId) {
        return userRepository.getUser(userId);
    }

    public Enrollment getUserEnrollment(String userId) {
        return userRepository.getUser(userId).getEnrollment();
    }

    /**
     * Get an IdemixEnrollment for an already enrolled HFUser (with a regular x509 Enrollment)
     *
     * @param user: existing user
     *
     * @return an IdemixEnrollment for the given user, issued by the specified CA
     *
     * @throws EnrollmentException
     * @throws InvalidArgumentException
     */
    public IdemixUser idemixEnroll(User user)
            throws EnrollmentException, InvalidArgumentException {
        Enrollment enrollment = hfcaClient.idemixEnroll(user.getEnrollment(), mspID);
        IdemixEnrollment idemixEnrollment = (IdemixEnrollment) enrollment;
        IdemixUser idemixUser = new IdemixUser(user.getName(), user.getMspId(), idemixEnrollment);
        userRepository.putIdemixUser(idemixUser);

        return idemixUser;
    }

//    /**
//     * Create an Idemix Pseudonym for an existing IdemixEnrollment Credential
//     * @param idemixEnrollment
//     * @return
//     */
//    public IdemixPseudonym idemixPseudonym(IdemixEnrollment idemixEnrollment) {
//
//        return new IdemixPseudonym(BIG.fromBytes(idemixEnrollment.getKey().getEncoded()),
//                idemixEnrollment.getIpk());
//    }

//    /**
//     * Sign a given message with a Zero-Knowledge Proof of the signature for an enrolled user's specific Pseudonym
//     * @param idemixEnrollment: existing IdemixEnrollment for a user
//     * @param idemixPseudonym: existing IdemixPseudonym to sign as
//     * @param message: message to sign
//     * @return
//     */
//    public IdemixSignature idemixSignature(IdemixEnrollment idemixEnrollment,
//                                           IdemixPseudonym idemixPseudonym, byte[] message) {
//
//        IdemixCredential cred = idemixEnrollment.getCred();
//        boolean[] disclosure = new boolean[cred.getAttrs().length];
//        // disclose all attributes
//        // TODO figure out which attributes to disclose
//        for (int i = 0 ; i < disclosure.length; i++)
//            disclosure[i] = true;
//
//        return new IdemixSignature(idemixEnrollment.getCred(),
//                BIG.fromBytes(idemixEnrollment.getKey().getEncoded()),
//                idemixPseudonym,
//                idemixEnrollment.getIpk(),
//                disclosure,
//                message,
//                0,
//                idemixEnrollment.getCri());
//    }

}
