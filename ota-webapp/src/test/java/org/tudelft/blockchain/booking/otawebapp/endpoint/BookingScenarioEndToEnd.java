package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BookingScenarioEndToEnd {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mvc;

    private void readCsv() {
        String csvFile = "";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] booking = line.split(cvsSplitBy);

                System.out.println("Booking [First ota = " + booking[0] + " , second=" + booking[1] + "]");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
