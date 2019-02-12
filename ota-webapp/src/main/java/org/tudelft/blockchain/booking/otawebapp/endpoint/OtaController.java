package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tudelft.blockchain.booking.otawebapp.model.Response;
import org.tudelft.blockchain.booking.otawebapp.service.BookingService;
import org.tudelft.blockchain.booking.otawebapp.service.PropertyService;

@RestController
@RequestMapping("api/ota")
public class OtaController {

    private final BookingService bookingService;

    private final PropertyService propertyService;

    @Autowired
    public OtaController(BookingService bookingService, PropertyService propertyService) {
        this.bookingService = bookingService;
        this.propertyService = propertyService;
    }


    @PostMapping("/{ota}/{propertyName}/book")
    public ResponseEntity book(
            @PathVariable("ota") String ota,
            @PathVariable("propertyName") String propertyName,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {
        Response serviceResponse = bookingService.book(ota, propertyName.toLowerCase(), fromDate, toDate);

        if (serviceResponse.getStatus() == Response.ResponseStatus.SUCCESS) {
            return ResponseEntity.ok(serviceResponse.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serviceResponse.getMessage());
        }
    }

    @GetMapping("/{ota}/{propertyName}/bookable")
    public ResponseEntity isBookable(
            @PathVariable("ota") String ota,
            @PathVariable("propertyName") String propertyName,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {

        Response serviceResponse = bookingService.isBookable(ota, propertyName.toLowerCase(), fromDate, toDate);

        if (serviceResponse.getStatus() == Response.ResponseStatus.SUCCESS) {
            return ResponseEntity.ok(serviceResponse.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serviceResponse.getMessage());
        }
    }

    @GetMapping("/{ota}/join/{propertyName}")
    public ResponseEntity joinChannel(@PathVariable("ota") String ota,
                                      @PathVariable("propertyName") String propertyName) {
        return ResponseEntity.ok(propertyService.joinProperty(ota, propertyName.toLowerCase()));
    }
}
