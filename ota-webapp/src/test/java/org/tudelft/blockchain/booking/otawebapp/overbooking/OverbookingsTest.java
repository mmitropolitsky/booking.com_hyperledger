package org.tudelft.blockchain.booking.otawebapp.overbooking;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tudelft.blockchain.booking.otawebapp.model.Booking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class OverbookingsTest {

    static List<Booking> bookings = new ArrayList<>();
    static int overbookingsCounter = 0;

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
    public void bookingsCheckFullOverbooking() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setStartDate(LocalDate.parse("2019-02-15"));
        booking1.setEndDate(LocalDate.parse("2019-02-18"));
        booking2.setStartDate(LocalDate.parse("2019-02-15"));
        booking2.setEndDate(LocalDate.parse("2019-02-18"));
        Assert.assertTrue(booking1.overbookingAttempt(booking2));
    }

    @Test
    public void bookingsCheckPartialOverbooking() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setStartDate(LocalDate.parse("2019-02-15"));
        booking1.setEndDate(LocalDate.parse("2019-02-18"));
        booking2.setStartDate(LocalDate.parse("2019-02-17"));
        booking2.setEndDate(LocalDate.parse("2019-02-18"));
        Assert.assertTrue(booking1.overbookingAttempt(booking2));
    }

    @Test
    public void bookingsCheckNoOverbooking() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setStartDate(LocalDate.parse("2019-02-15"));
        booking1.setEndDate(LocalDate.parse("2019-02-18"));
        booking2.setStartDate(LocalDate.parse("2019-02-19"));
        booking2.setEndDate(LocalDate.parse("2019-02-20"));
        Assert.assertFalse(booking1.overbookingAttempt(booking2));
    }

    @Test
    public void bookingsCheckEdgeNoOverbooking() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setStartDate(LocalDate.parse("2019-02-15"));
        booking1.setEndDate(LocalDate.parse("2019-02-18"));
        booking2.setStartDate(LocalDate.parse("2019-02-18"));
        booking2.setEndDate(LocalDate.parse("2019-02-19"));
        Assert.assertFalse(booking1.overbookingAttempt(booking2));
    }

    @Test
    public void bookingsCheckEdgeOverbooking() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setStartDate(LocalDate.parse("2019-02-15"));
        booking1.setEndDate(LocalDate.parse("2019-02-18"));
        booking2.setStartDate(LocalDate.parse("2019-02-17"));
        booking2.setEndDate(LocalDate.parse("2019-02-19"));
        Assert.assertTrue(booking1.overbookingAttempt(booking2));
    }

    @Test
    public void findOverbookings() {

        bookings.stream().collect(groupingBy(Booking::getPropertyName)).entrySet().forEach(
                (entry) -> {
                    List<Booking> booked = new ArrayList<>();
                    List<Booking> notProcessed = entry.getValue();
                    booked.add(notProcessed.get(0));
                    notProcessed.remove(0);
                    int counter = 0;
                    for (int i = 0; i < notProcessed.size(); i++) {

                        boolean isOverbookingAttempt = false;
                        for (int j = 0; j < booked.size(); j++) {
                            if (booked.get(j).overbookingAttempt(notProcessed.get(i))) {
                                isOverbookingAttempt = true;
                                counter++;
                                overbookingsCounter++;
                                break;
                            }
                        }
                        if (!isOverbookingAttempt) {
                            booked.add(notProcessed.get(i));
                        }
                    }
                    System.out.println("Overbookings for [" + entry.getKey() + "] = " + counter);


                }
        );

        System.out.println("Encountered overbookings: " + overbookingsCounter);
    }
}
