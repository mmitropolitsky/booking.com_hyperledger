package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.tudelft.blockchain.booking.otawebapp.model.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookingScenarioEndToEnd {

    private static int INTERNAL_SERVER_ERROR = 500;

    @Autowired
    private MockMvc mvc;

    protected static List<Booking> bookings = new ArrayList<>();

    private static int counter = 0;
    private static List<Booking> overbookings = new ArrayList<>();


    @Test
    public void AcreateProperties() {
        List<String> properties = bookings.stream().map(Booking::getPropertyName).distinct()
                .collect(Collectors.toList());
        properties.forEach(p -> {
                    try {
                        mvc.perform(post("/api/po/property").param("name", p)).andExpect(status().isOk());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        );
    }

    @Test
    public void BotaAJoinProperties() throws Exception {
        joinProperty("OtaA");
    }

    @Test
    public void CotaBJoinProperties() throws Exception {
        joinProperty("OtaB");
    }

    private void joinProperty(String orgName) throws Exception {
        List<String> otaProperties = bookings.stream().filter(b -> b.getOtaName().equals(orgName)).map(Booking::getPropertyName).distinct()
                .collect(Collectors.toList());
        otaProperties.forEach(p -> {
                    try {
                        System.out.println("Org [" + orgName + "] joining property [" + p + "]");
                        mvc.perform(get("/api/ota/" + orgName + "/join/" + p)).andExpect(status().isOk());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        Thread.sleep(5000);
        mvc.perform(get("/api/ota/" + orgName + "/" + otaProperties.get(0) + "/bookable").param("fromDate", "2019-10-05").param("toDate", "2019-10-10")).andExpect(status().isOk());
        Thread.sleep(10000);
    }

    @Test
    public void detectOverbookings() {
        bookings.forEach(booking -> {
                    try {
                        Thread.sleep(2500);
                        ResultActions resultActions = mvc.perform(post("/api/ota/" + booking.getOtaName() + "/" + booking.getPropertyName() + "/book/")
                                .param("fromDate", booking.getStartDate().toString()).param("toDate", booking.getEndDate().toString()));
                        System.out.println("Process booking " + booking.getId());
                        if (resultActions.andReturn().getResponse().getStatus() == INTERNAL_SERVER_ERROR) {
                            System.out.println("Caught overbooking " + booking);
                            counter++;
                            overbookings.add(booking);
                            System.out.println("Current overbooking count " + counter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        );
        System.out.println("Detected overbookings: " + counter);
        System.out.println("Overbookings ids: " + overbookings.stream().map(Booking::getId).collect(Collectors.toList()));

    }

}
