package org.tudelft.blockchain.booking.otawebapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.Response;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.BookingRepository;

@Component
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Response isBookable(String orgName, String propertyName, String fromDate, String toDate) {
        try {
            return bookingRepository.isBookable(orgName, propertyName, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(e.getMessage(), Response.ResponseStatus.FAILURE);
        }

    }

    public Response book(String orgName, String propertyName, String fromDate, String toDate) {
        try {
            return bookingRepository.book(orgName, propertyName, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(e.getMessage(), Response.ResponseStatus.FAILURE);
        }
    }

}
