package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.tudelft.blockchain.booking.otawebapp.model.Property;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.AvailabilityStatus;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PropertyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllPropertiesShouldReturnEmptyList() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/api/properties",
                ArrayList.class)).isEqualTo(new ArrayList<>());
    }

    @Test
    public void getPropertyByIdReturnDefaultProperty() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/api/properties/1",
                Property.class)).isEqualTo(getDefaultProperty());
    }

    @Test
    public void getAvailableDatesShouldReturnPredefinedPairs() {
        ResponseEntity<List<DateAvailabilityPair>> response = restTemplate.exchange("http://localhost:" + port + "/api/properties/1/availability", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<DateAvailabilityPair>>() {});
        assertThat(response.getBody()).isEqualTo(getAvailabilityPair());
    }

    private static Property getDefaultProperty () {
        return new Property("1", "address", "desc");
    }

    private static Collection<DateAvailabilityPair> getAvailabilityPair() {
        List<DateAvailabilityPair> dateAvailabilityPairs = new ArrayList<>();
        dateAvailabilityPairs
                .add(new DateAvailabilityPair(LocalDate.now(), AvailabilityStatus.AVAILABLE));
        dateAvailabilityPairs
                .add(new DateAvailabilityPair(LocalDate.now().plusDays(1), AvailabilityStatus.BOOKED));
        dateAvailabilityPairs
                .add(new DateAvailabilityPair(LocalDate.now().plusDays(2), AvailabilityStatus.AVAILABLE));
        return dateAvailabilityPairs;
    }
}
