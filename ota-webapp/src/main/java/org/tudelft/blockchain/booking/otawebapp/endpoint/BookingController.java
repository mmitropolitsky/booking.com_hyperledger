package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tudelft.blockchain.booking.otawebapp.service.BookingService;
import org.tudelft.blockchain.booking.otawebapp.service.ChainCodeService;

@RestController
@RequestMapping("api/booking")
public class BookingController {
    @Autowired
    private ChainCodeService chainCodeService;

    @Autowired
    private BookingService bookingService;


    @GetMapping("book")
    public boolean book(@RequestParam(value = "fromDate") String fromDate, @RequestParam("toDate") String toDate) {
        try {
            return bookingService.book(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("isBookable")
    public boolean isBookable(@RequestParam(value = "fromDate") String fromDate, @RequestParam("toDate") String toDate) {
        try {
            return bookingService.isBookable(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("install")
    public void installChaincode() {
        try {
            chainCodeService.deployAndInstantiateChainCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
