package org.tudelft.blockchain.booking.otawebapp.service;

import org.apache.milagro.amcl.FP256BN.BIG;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.idemix.IdemixCredential;
import org.hyperledger.fabric.sdk.idemix.IdemixPseudonym;
import org.hyperledger.fabric.sdk.idemix.IdemixSignature;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.CredentialRepository;

@Component
public class CredentialService {

    @Autowired
    CredentialRepository credentialRepository;

    public String registerUser(String userId)
            throws RegistrationException, InvalidArgumentException {

        return credentialRepository.registerUser(userId);
    }

    public Enrollment enrollUser(String userId, String userSecret)
            throws EnrollmentException, InvalidArgumentException {
        return credentialRepository.enrollUser(userId, userSecret);
    }

    public HFUser getUser(String userId) {
        return credentialRepository.getUser(userId);
    }

    public Enrollment getUserEnrollment(String userId) {
        return credentialRepository.getUser(userId).getEnrollment();
    }

    public IdemixEnrollment idemixEnroll(Enrollment userEnrollment)
        throws EnrollmentException, InvalidArgumentException {

        return credentialRepository.idemixEnroll(userEnrollment);
    }

    public IdemixEnrollment idemixEnroll(HFUser user)
        throws EnrollmentException, InvalidArgumentException {

        return credentialRepository.idemixEnroll(user.getEnrollment());
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
