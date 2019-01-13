package org.tudelft.blockchain.booking.ca;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CaApplication {

    public static void main(String[] args) {

        SpringApplication.run(CaApplication.class, args);

    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
//
//    @Bean
//    public ObjectMapper getObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        return objectMapper;
//    }

}
