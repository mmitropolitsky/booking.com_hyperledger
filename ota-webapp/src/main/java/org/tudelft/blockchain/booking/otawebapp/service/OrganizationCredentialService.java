package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.CAEnrollment;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.RolesSet;
import org.tudelft.blockchain.booking.otawebapp.util.OrgStringBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Component
public class OrganizationCredentialService {

    @Value("${org.tudelft.blockchain.booking.admin.username}")
    private String adminUsername;

    @Value("${org.tudelft.blockchain.booking.admin.password}")
    private String adminPassword;

    @Value("${org.tudelft.blockchain.booking.peer.orgs.path}")
    private String peerOrganizationCryptoPath;

    private final OrgStringBuilder orgStringBuilder;


    // TODO this should be in repository, maybe in a database
    private Map<String, User> preparedOrgAdmins = new HashMap<>();

    private Map<String, User> preparedCaAdmins = new HashMap<>();

    private Map<String, User> preparedUsers = new HashMap<>();

    private Map<String, HFCAClient> orgHfcaClients = new HashMap<>();

    @Autowired
    public OrganizationCredentialService(OrgStringBuilder orgStringBuilder) {
        this.orgStringBuilder = orgStringBuilder;
    }


    /**
     * Setup org admin
     *
     * @param orgName
     */
    private void setupOrgAdmin(String orgName) {
        String domainName = orgStringBuilder.getDomainName(orgName);
        File pkFolder = new File(buildAdminMspPrivateKeyPath(domainName));
        File[] pkFiles = pkFolder.listFiles();

        File certFolder = new File(buildAdminMspCertificatePath(domainName));
        File[] certFiles = certFolder.listFiles();

        CAEnrollment enrollment = null;
        try {
            enrollment = getEnrollment(pkFolder.getPath(),
                    Objects.requireNonNull(pkFiles)[0].getName(), certFolder.getPath(), Objects.requireNonNull(certFiles)[0].getName());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        preparedOrgAdmins.put(orgName, new HFUser("admin", orgName, orgStringBuilder.getMspId(orgName), enrollment));
    }

    /**
     * Setup org admin
     *
     * @param username
     * @param password
     * @param orgName
     * @throws EnrollmentException
     * @throws InvalidArgumentException
     */
    private void setupCaAdmin(String username, String password, String orgName) throws Exception {
        HFCAClient hfcaClient = getHfcaClient(orgName);
        Enrollment adminEnrollment = hfcaClient.enroll(username, password);
        HFUser caAdmin = new HFUser("admin", orgName, orgStringBuilder.getMspId(orgName), adminEnrollment);
        Set<String> roles = new RolesSet();
        roles.add("admin");
        caAdmin.setRoles(roles);

        if (hfcaClient.getHFCAAffiliations(caAdmin).getChildren().stream().noneMatch(aff -> aff.getName().equals(orgName))) {
            hfcaClient.newHFCAAffiliation(orgName).create(caAdmin);
        }
        preparedCaAdmins.put(orgName, caAdmin);
    }


    /**
     * Setup org admin with default username/password
     *
     * @param orgName
     * @throws EnrollmentException
     * @throws InvalidArgumentException
     */
    private void setupCaAdmin(String orgName) throws Exception {
        setupCaAdmin(adminUsername, adminPassword, orgName);
    }

    /**
     * Register and save user
     *
     * @param orgName
     * @param username
     * @throws Exception
     */
    private void setupUser(String orgName, String username) throws Exception {
        HFCAClient hfcaClient = getHfcaClient(orgName);
        User registrar = getCaAdmin(orgName);
//        if (hfcaClient.getHFCAIdentities(registrar).stream().noneMatch(identity -> username.equals(identity.getEnrollmentId()))) {
        RegistrationRequest registrationRequest = new RegistrationRequest(username, orgName);
        String secret = hfcaClient.register(registrationRequest, registrar);
//        }
        Enrollment enrollment = hfcaClient.enroll(username, secret);
        Enrollment idemixEnrollment = hfcaClient.idemixEnroll(enrollment, orgStringBuilder.getMspId(orgName));
        HFUser user = new HFUser(username, orgName, orgStringBuilder.getMspId(orgName), idemixEnrollment);
//        HFUser user = new HFUser(username, orgName, orgStringBuilder.getMspId(orgName), enrollment);

        Set<String> roles = new RolesSet();
        roles.add("member");
        user.setRoles(roles);
        preparedUsers.put(orgName, user);
    }

    /**
     * Register a user with a default name
     *
     * @param orgName
     * @throws Exception
     */
    private void setupUser(String orgName) throws Exception {
        setupUser(orgName, "user_" + orgName);
    }

    /**
     * Setup the HFCA Client
     *
     * @throws Exception
     */
    private void setupHfCaClient(String orgName) throws Exception {
        // cf Fabric Java SDK source code for HFCAClient for documentation on supported Properties
        Properties properties = new Properties();
        properties.setProperty("allowAllHostnames", "true");

        HFCAClient hfcaClient = HFCAClient.createNewInstance(orgStringBuilder.getCaUrl(orgName), properties);
        // Set crypto primitives to the default crypto primitives returned by the factory
        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

//        hfcaClient.newHFCAAffiliation(orgName);
        orgHfcaClients.put(orgName, hfcaClient);
    }

    private HFCAClient getHfcaClient(String orgName) throws Exception {
        if (!orgHfcaClients.containsKey(orgName)) {
            setupHfCaClient(orgName);
        }

        return orgHfcaClients.get(orgName);
    }

    public User getOrgAdmin(String orgName) {
        if (!preparedOrgAdmins.containsKey(orgName)) {
            setupOrgAdmin(orgName);
        }


        return preparedOrgAdmins.get(orgName);
    }

    private User getCaAdmin(String orgName) throws Exception {
        if (!preparedCaAdmins.containsKey(orgName)) {
            setupCaAdmin(orgName);
        }

        return preparedCaAdmins.get(orgName);
    }

    public User getUser(String orgName) throws Exception {
        if (!preparedUsers.containsKey(orgName)) {
            setupUser(orgName);
        }

        return preparedUsers.get(orgName);
    }

    private String buildAdminCertsPath(String domainName) {
        return peerOrganizationCryptoPath + File.separator + domainName + File.separator + "users/Admin@" + domainName + File.separator;
    }

    private String buildAdminMspPrivateKeyPath(String domainName) {
        return buildAdminCertsPath(domainName) + "msp/keystore";
    }

    private String buildAdminMspCertificatePath(String domainName) {
        return buildAdminCertsPath(domainName) + "msp/admincerts";
    }

    private CAEnrollment getEnrollment(String keyFolderPath, String keyFileName, String certFolderPath, String certFileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey key;
        String certificate;

        try (InputStream isKey = new FileInputStream(keyFolderPath + File.separator + keyFileName);
             BufferedReader brKey = new BufferedReader(new InputStreamReader(isKey))) {

            StringBuilder keyBuilder = new StringBuilder();

            for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
                if (!line.contains("PRIVATE")) {
                    keyBuilder.append(line);
                }
            }

            certificate = new String(Files.readAllBytes(Paths.get(certFolderPath, certFileName)));

            byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");
            key = kf.generatePrivate(keySpec);
        }

        return new CAEnrollment(key, certificate);
    }
}
