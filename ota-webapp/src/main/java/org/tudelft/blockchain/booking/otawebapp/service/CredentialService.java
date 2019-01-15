package org.tudelft.blockchain.booking.ca.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tudelft.blockchain.booking.ca.model.NonceResponse;

import java.util.Arrays;

@Component
public class CredentialService {

    private static final String USERNAME = "admin2";
    private static final String PASSWORD = "xsTdJWcnDydn";
    private static final String LOCALHOST = "http://localhost";
    private static final String PORT = ":7054";
    private static final String CREDENTIAL_ENDPOINT = "/api/v1/idemix/credential";


    @Autowired
    RestTemplate restTemplate;

    public NonceResponse getNonceResponse() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(USERNAME, PASSWORD);
        HttpEntity<String> entity = new HttpEntity<String>("{}", headers);

        return restTemplate.exchange(LOCALHOST + PORT + CREDENTIAL_ENDPOINT, HttpMethod.POST, entity, NonceResponse.class).getBody();
    }
}
