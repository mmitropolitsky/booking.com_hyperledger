package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdk.user.IdemixUser;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tudelft.blockchain.booking.otawebapp.util.Util;

import java.io.File;
import java.security.PrivateKey;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CredentialServiceTest {

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

    @Autowired
    CredentialService credentialService;

    private void revoke(String username) throws Exception {
        credentialService.revoke(username, "cleanup");
    }

    @Test
    public void testSetupCaAdmin() throws Exception {
        User caAdmin = credentialService.getOrgAdminUser();

        assertEquals("admin", caAdmin.getName());
        assertEquals(orgName, caAdmin.getAffiliation());
        assertEquals(mspID, caAdmin.getMspId());
        assertTrue(caAdmin.getRoles().contains("admin"));
    }


    @Test
    public void testRegisterNewUser() throws Exception {
        String username = "user1";
        String secret = credentialService.registerUser(username);
        assertNotNull(secret);
        revoke(username);
    }

    @Test(expected = RegistrationException.class)
    public void testRegisterAlreadyExistingUser() throws Exception {
        String username = "user2";
        String secret = credentialService.registerUser(username);
        String shallNotPass = credentialService.registerUser(username);
    }

    @Test
    public void testEnrollUser() throws Exception {
        String username = "user3";
        String secret = credentialService.registerUser(username);
        User user = credentialService.getEnrolledUser(username, secret);

        assertEquals(username, user.getName());
        assertEquals(mspID, user.getMspId());
        assertEquals(orgName, user.getAffiliation());
        //TODO test roles
        revoke(username);
    }

    @Test
    public void getUser() throws Exception {
        String username = "user4";
        String secret = credentialService.registerUser(username);
        User user = credentialService.getEnrolledUser(username, secret);
        User user4 = credentialService.getUser(username);

        assertEquals(user, user4);
    }

    @Test
    public void getUserEnrollment() throws Exception {
        String username = "user5";
        String secret = credentialService.registerUser(username);
        User user = credentialService.getEnrolledUser(username, secret);
        Enrollment enrollment = credentialService.getUserEnrollment(username);

        assertEquals(user.getEnrollment(), enrollment);
    }

    @Test
    public void idemixEnroll() throws Exception {
        String username = "user6";
        String secret = credentialService.registerUser(username);
        User idemixUser = credentialService.getIdemixEnrolledUser(username, secret);
        User user = credentialService.getUser(username);

//        assertNotEquals(user.getName(), idemixUser.getName());
        assertNotEquals(user.getEnrollment().getKey(), idemixUser.getEnrollment().getKey());
        //TODO reflect changes when adding third party CA
        assertEquals(mspID, idemixUser.getMspId());
    }
}