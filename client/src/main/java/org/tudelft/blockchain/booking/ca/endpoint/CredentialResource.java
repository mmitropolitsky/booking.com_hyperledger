package org.tudelft.blockchain.booking.ca.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tudelft.blockchain.booking.ca.model.NonceResponse;
import org.tudelft.blockchain.booking.ca.service.CredentialService;

@RestController
public class CredentialResource {
    @Autowired
    CredentialService credentialService;

    @RequestMapping(value = "api/nonce", method = RequestMethod.POST)
    public NonceResponse retreiveNonce() {
        return credentialService.getNonceResponse();
    }
}
