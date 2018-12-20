package org.tudelft.blockchain.booking.otawebapp.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tudelft.blockchain.booking.otawebapp.model.Property;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PropertyRepositoryUnitTest {

    private List<Property> dummyPropertyList = new ArrayList<>();

    @Before
    public void setupProperties() {
        for (int i = 0; i < 10; i++) {
            dummyPropertyList.add(new Property("id" + i, "address" + i, "description" + i));
        }
    }

    @Test
    public void getAllPropertiesTest() {

    }

    @Test
    public void getPropertyByIdTest() {

    }

    @Test
    public void getAvailableDates() {

    }
}
