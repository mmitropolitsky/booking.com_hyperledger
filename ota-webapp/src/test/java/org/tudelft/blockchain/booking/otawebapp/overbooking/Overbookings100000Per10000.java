package org.tudelft.blockchain.booking.otawebapp.overbooking;

import org.junit.BeforeClass;
import org.tudelft.blockchain.booking.otawebapp.model.Booking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class Overbookings100000Per10000 extends OverbookingsTest {

    @BeforeClass
    public static void loadData() {
        readCsv();
    }

    protected static void readCsv() {
        String csvFile = "bookings_100000_per_10000.csv";
        String line = "";
        String cvsSplitBy = ",";

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(csvFile);
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);

        try (BufferedReader br = new BufferedReader(streamReader)) {
            br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] bookingArray = line.split(cvsSplitBy);
                Booking booking = new Booking();
                booking.setId(Long.parseLong(bookingArray[5]));
                booking.setOtaName("Ota" + bookingArray[1]);
                booking.setPropertyName(bookingArray[3].replaceAll("_", ""));
                booking.setStartDate(LocalDate.parse(bookingArray[4]));
                booking.setEndDate(LocalDate.parse(bookingArray[2]));
                booking.setStatus(bookingArray[0]);
                bookings.add(booking);

            }
            System.out.println("Overbookings count " + bookings.stream().filter(b -> b.getStatus().equals("overbooking")).count());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
