package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.junit.BeforeClass;
import org.tudelft.blockchain.booking.otawebapp.model.Booking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class BookingEndToEnd500Per50 extends BookingScenarioEndToEnd {

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

                // use semicolon as separator
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
}
