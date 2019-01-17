package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tudelft.blockchain.booking.otawebapp.service.CredentialService;

@RestController
@RequestMapping("/api/ca")
public class CredentialController {

    @Autowired
    CredentialService credentialService;

    @GetMapping("/register/{userID}")
    public String registerUser(
            @PathVariable("userID") String userID) {
        try {
            return credentialService.registerUser(userID);
        } catch (RegistrationException | InvalidArgumentException e) {
            return e.getMessage();
        }
    }

    @GetMapping("/enroll/{userID}")
    public Enrollment enrollUser(
            @PathVariable("userID") String userId,
            @RequestParam(required = true) String userSecret ) {
        try {
            return credentialService.enrollUser(userId, userSecret);
        } catch (EnrollmentException | InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/{userID}")
    public Enrollment getUser(
            @PathVariable("userID") String userId) {
        return credentialService.getUserEnrollment(userId);
    }

    @GetMapping("/idemix/enroll/{userID}")
    public IdemixEnrollment idemixEnroll(
            @PathVariable("userID") String userId) {
        try {
            IdemixEnrollment idemixEnrollment = credentialService.idemixEnroll(credentialService.getUser(userId));
            return idemixEnrollment;
        } catch (EnrollmentException | InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

//    @GetMapping("/{userID}")
//    public boolean getIdemixEnrollment(
//            @PathVariable("userID") String userID) {
//        try {
//            String caURL = "http://localhost:7054";
//            HFUser admin = credentialService.(caURL);
////                System.out.println("name : " + admin.getName());
////                System.out.println("roles : " + admin.getRoles());
////                System.out.println("account : " + admin.getAccount());
////                System.out.println("MSP ID : " + admin.getMspId());
////                System.out.println("Certificate :\n" + admin.getEnrollment().getCert());
//
//            IdemixEnrollment idemixEnrollment = credentialService.idemixEnroll(admin, caURL);
//
//            // check that the Idemix Issuer Public Key is valid
//            assert idemixEnrollment.getIpk().check();
//            return idemixEnrollment != null;
//            // get the CA client
////                HFCAClient hfcaClient = credentialService.getCertificateAuthority(caURL);
//            // check the IdemixCredentials validity
////                assert idemixEnrollment.getCred().verify(BIG.fromBytes(admin.getEnrollment().getKey().getEncoded()), hfcaClient.info().getIdemixIssuerPublicKey());
//
////            IdemixPseudonym pseudonym = credentialService.idemixPseudonym(idemixEnrollment);
//
////            assert pseudonym != null;
////            String message = "Hello World !";
////            IdemixSignature signedMessage = credentialService.idemixSignature(idemixEnrollment, pseudonym, message.getBytes());
////
////            return signedMessage != null;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

}

