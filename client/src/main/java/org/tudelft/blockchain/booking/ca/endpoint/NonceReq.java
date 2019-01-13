package org.tudelft.blockchain.booking.client.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@RestController
public class NonceReq {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "api/nonce", method = RequestMethod.POST)
    public NonceResponse retreiveNonce() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth("admin2","xsTdJWcnDydn");
        HttpEntity<String> entity = new HttpEntity<String>("{}",headers);

        return restTemplate.exchange(
                "http://localhost:7054/api/v1/idemix/credential", HttpMethod.POST, entity, NonceResponse.class).getBody();
    }
}

