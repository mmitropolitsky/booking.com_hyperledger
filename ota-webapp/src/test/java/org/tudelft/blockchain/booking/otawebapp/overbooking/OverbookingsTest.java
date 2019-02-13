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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class OverbookingsTest {

    protected static List<Booking> bookings = new ArrayList<>();
    private static int overbookingsCounter = 0;
    private static List<Booking> overbookings = new ArrayList<>();


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
                                overbookings.add(notProcessed.get(i));
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
        System.out.println("Overbookings' ids: " + overbookings.stream().map(Booking::getId).sorted().collect(Collectors.toList()));
    }
}
