package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.user.IdemixUser;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.hyperledger.fabric_ca.sdk.exception.RevocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.RolesSet;
import org.tudelft.blockchain.booking.otawebapp.repository.UserRepository;
import org.tudelft.blockchain.booking.otawebapp.util.Util;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Component
public class CredentialService {

    @Autowired
    UserRepository userRepository;

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    protected String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.password}")
    protected String adminPassword;

    @Value("${org.tudelft.blockchain.booking.ca.url}")
    protected String caURL;

    @Value("${org.tudelft.blockchain.booking.ca.orgname}")
    protected String orgName;

    @Value("${org.tudelft.blockchain.booking.ca.mspid}")
    protected String mspID;

    @Value("${org.tudelft.blockchain.booking.admin.private.key.path}")
    String adminPrivateKeyPath;

    @Value("${org.tudelft.blockchain.booking.admin.certificate.path}")
    String adminCertificatePath;


    private HFCAClient hfcaClient;

    private HFUser caAdmin;

    private HFUser orgAdmin;

    @PostConstruct
    public void setUp() {
        try {
            setupHfCaClient();

            setupCaAdmin();

            setupOrgAdmin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupHfCaClient() throws Exception {
        // cf Fabric Java SDK source code for HFCAClient for documentation on supported Properties
        Properties properties = new Properties();
        properties.setProperty("allowAllHostnames", "true");

        hfcaClient = HFCAClient.createNewInstance(caURL, properties);
        // Set crypto primitives to the default crypto primitives returned by the factory
        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
    }

    private void setupOrgAdmin() {
        File pkFolder = new File(adminPrivateKeyPath);
        File[] pkFiles = pkFolder.listFiles();

        File certFolder = new File(adminCertificatePath);
        File[] certFiles = certFolder.listFiles();

//        CAEnrollment enrollment = Util.getEnrollment(pkFolder.getPath(), pkFiles[0].getName(), certFolder.getPath(), certFiles[0].getName());
        try {
            Enrollment enrollment = new X509Enrollment(
                    Util.parsePrivateKey(pkFolder.getPath(), pkFiles[0].getName()),
                    Util.parseCertificate(certFolder.getPath(), certFiles[0].getName()));

            orgAdmin = new HFUser(adminUsername, orgName, mspID, enrollment);
            Set<String> roles = new RolesSet();
            roles.add("admin");
            orgAdmin.setRoles(roles);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void setupCaAdmin() throws EnrollmentException, InvalidArgumentException {
        Enrollment adminEnrollment = hfcaClient.enroll(adminUsername, adminPassword);
        caAdmin = new HFUser(adminUsername, orgName, mspID, adminEnrollment);
        Set<String> roles = new RolesSet();
        roles.add("admin");
        caAdmin.setRoles(roles);
    }

    /**
     * Register a new User for a given organization
     *
     * @param username
     * @return the User Secret generated i.e. password
     * @throws Exception
     */
    public String registerUser(String username)
            throws RegistrationException, InvalidArgumentException {
        try {
            RegistrationRequest registrationRequest = new RegistrationRequest(username, orgName);
//            registrationRequest.setType(HFCAClient.HFCA_TYPE_CLIENT);
            String secret = hfcaClient.register(registrationRequest, caAdmin);
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
     *
     * @param username
     * @param userSecret
     * @return
     * @throws Exception
     */
    public User getEnrolledUser(String username, String userSecret)
            throws EnrollmentException, InvalidArgumentException {
        Enrollment enrollment = hfcaClient.enroll(username, userSecret);
        HFUser user = new HFUser(username, orgName, mspID, enrollment);

        Set<String> roles = new RolesSet();
        roles.add("member");
        user.setRoles(roles);


        userRepository.putUser(user);
        return user;
    }

    public User getUser(String userId) {
        return userRepository.getUser(userId);
    }

    @Deprecated
    public Enrollment getUserEnrollment(String userId) {
        return userRepository.getUser(userId).getEnrollment();
    }

    public User getCaAdminUser() {
        return caAdmin;
    }

    public User getOrgAdminUser() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        return orgAdmin;
    }

    /**
     * Get an IdemixEnrollment for an already enrolled HFUser (with a regular x509 Enrollment)
     *
     * @param user: existing user
     * @return an IdemixEnrollment for the given user, issued by the specified CA
     * @throws EnrollmentException
     * @throws InvalidArgumentException
     */
    private IdemixUser getIdemixEnrolledUser(User user)
            throws EnrollmentException, InvalidArgumentException {
        Enrollment enrollment = hfcaClient.idemixEnroll(user.getEnrollment(), mspID);
        IdemixEnrollment idemixEnrollment = (IdemixEnrollment) enrollment;
        IdemixUser idemixUser = new IdemixUser(user.getName(), user.getMspId(), idemixEnrollment);
        userRepository.putIdemixUser(idemixUser);

        return idemixUser;
    }

    public IdemixUser getIdemixEnrolledUser(String username, String password) throws Exception {
        return this.getIdemixEnrolledUser(this.getEnrolledUser(username, password));
    }

    public void revoke(String username, String reason) throws InvalidArgumentException, RevocationException {
        hfcaClient.revoke(caAdmin, username, reason);
    }

    public void revoke(Enrollment enrollment, String reason) throws InvalidArgumentException, RevocationException {
        hfcaClient.revoke(caAdmin, enrollment, reason);
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
