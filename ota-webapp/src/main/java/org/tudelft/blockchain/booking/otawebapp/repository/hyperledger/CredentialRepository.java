package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;

import javax.annotation.PostConstruct;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class CredentialRepository {

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

    private Map<String, HFUser> users;

    @PostConstruct
    public void setUp() {
        try {
            // cf Fabric Java SDK source code for HFCAClient for documentation on supported Properties
            Properties properties = new Properties();
            properties.setProperty("allowAllHostnames", "true");

            hfcaClient = HFCAClient.createNewInstance(caURL, null);
            // Set crypto primitives to the default crypto primitives returned by the factory
            hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

            Enrollment adminEnrollment = hfcaClient.enroll(adminUsername, adminPassword);
            admin = new HFUser(adminUsername, orgName, mspID, adminEnrollment);

            users = new IdentityHashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a new User for a given organization
     * @param userId
     * @return the User Secret generated i.e. password
     * @throws Exception
     */
    public String registerUser(String userId)
            throws RegistrationException, InvalidArgumentException {
        try {
            RegistrationRequest registrationRequest = new RegistrationRequest(userId, orgName);
            String secret = hfcaClient.register(registrationRequest, admin);
            users.put(userId, null);
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
     * @param userId
     * @param userSecret
     * @return
     * @throws Exception
     */
    public Enrollment enrollUser(String userId, String userSecret)
            throws EnrollmentException, InvalidArgumentException {
        Enrollment enrollment = hfcaClient.enroll(userId, userSecret);
        users.replace(userId, new HFUser(userId, orgName, mspID, enrollment));
        return enrollment;
    }

    /**
     * Get an IdemixEnrollment for an already enrolled HFUser (with a regular x509 Enrollment)
     *
     * @param userEnrollment: existing user enrollment
     *
     * @return an IdemixEnrollment for the given user, issued by the specified CA
     *
     * @throws EnrollmentException
     * @throws InvalidArgumentException
     */
    public IdemixEnrollment idemixEnroll(Enrollment userEnrollment)
            throws EnrollmentException, InvalidArgumentException {
        Enrollment idemixEnrollment = hfcaClient.idemixEnroll(userEnrollment, mspID);
        return (IdemixEnrollment) idemixEnrollment;
    }

    /**
     * Get the HFUser instance corresponding to an enrolled user
     * @param userId
     * @return
     */
    public HFUser getUser(String userId) {
        return users.get(userId);
    }

}
