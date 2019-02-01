package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tudelft.blockchain.booking.otawebapp.service.BookingService;
import org.tudelft.blockchain.booking.otawebapp.service.PropertyService;

@RestController
@RequestMapping("api/ota")
public class OtaController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PropertyService propertyService;


    @PostMapping("/{ota}/{propertyName}/book")
    public boolean book(
            @PathVariable("ota") String ota,
            @PathVariable("propertyName") String propertyName,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {
        try {
            System.out.println(ota);
            return bookingService.book(ota, propertyName.toLowerCase(), fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/{ota}/{propertyName}/bookable")
    public boolean isBookable(
            @PathVariable("ota") String ota,
            @PathVariable("propertyName") String propertyName,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {
        try {
            return bookingService.isBookable(ota, propertyName.toLowerCase(), fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/{ota}/join/{propertyName}")
    public void joinChannel(@PathVariable("ota") String ota,
                            @PathVariable("propertyName") String propertyName) {
        // TODO fix chaincode instantiation
        try {
            propertyService.joinProperty(ota, propertyName.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
