package org.tudelft.blockchain.booking.otawebapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.BookingRepository;

@Component
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    public boolean isBookable(String fromDate, String toDate) {

        try {
            return bookingRepository.isBookable(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean book(String fromDate, String toDate) {
        try {
            return bookingRepository.book(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
