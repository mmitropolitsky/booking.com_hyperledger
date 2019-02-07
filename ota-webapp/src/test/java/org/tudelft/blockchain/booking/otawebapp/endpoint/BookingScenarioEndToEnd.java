package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.junit.BeforeClass;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookingScenarioEndToEnd {

    @Autowired
    private MockMvc mvc;

    private static List<Booking> bookings = new ArrayList<>();

    private static int counter = 0;

    @BeforeClass
    public static void loadData() {
        readCsv();
    }

    private static void readCsv() {
        String csvFile = "bookings_500_per_50_filter.csv";
        String line = "";
        String cvsSplitBy = ";";

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(csvFile);
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);

        try (BufferedReader br = new BufferedReader(streamReader)) {
            br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] bookingArray = line.split(cvsSplitBy);
                Booking booking = new Booking();
                booking.setId(Long.parseLong(bookingArray[0]));
                booking.setOtaName("Ota" + bookingArray[1]);
                booking.setPropertyName(bookingArray[2].replaceAll("_", ""));
                booking.setStartDate(LocalDate.parse(bookingArray[3]));
                booking.setEndDate(LocalDate.parse(bookingArray[4]));
                bookings.add(booking);
                System.out.println(booking);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                        mvc.perform(get("/api/ota/" + orgName + "/join/" + p)).andExpect(status().isOk());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

//        mvc.perform(get("/api/ota/" + orgName + "/property0/isBookable").param("fromDate", "2019-10-05").param("toDate", "2019-10-10")).andExpect(status().isOk());

    }

    @Test
    public void detectOverbookings() throws Exception {
        Thread.sleep(10000);
        bookings.forEach(booking -> {
                    try {
                        ResultActions resultActions = mvc.perform(post("/api/ota/" + booking.getOtaName() + "/" + booking.getPropertyName() + "/book/")
                                .param("fromDate", booking.getStartDate().toString()).param("toDate", booking.getEndDate().toString())).andDo(print())
                                .andExpect(status().isOk());
                        System.out.println("Process booking " + booking.getId());
                        if (!Boolean.valueOf(resultActions.andReturn().getResponse().getContentAsString())) {
                            System.out.println("Caught overbooking " + booking);
                            counter++;
                            System.out.println("Current overbooking count " + counter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        );
        System.out.println("Detected overbookings: " + counter);

    }

}
