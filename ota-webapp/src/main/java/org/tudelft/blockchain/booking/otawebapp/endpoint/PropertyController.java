package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.springframework.web.bind.annotation.*;
import org.tudelft.blockchain.booking.otawebapp.model.Property;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;
import org.tudelft.blockchain.booking.otawebapp.service.PropertyService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private PropertyService propertyService;


    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }


    @GetMapping("/{propertyId}")
    public Property getPropertyById(@PathVariable("propertyId") String propertyId) {
        return new Property(propertyId, "address", "desc");
    }

    @GetMapping("/{propertyId}/availability")
    public Collection<DateAvailabilityPair> getAvailability(
            @PathVariable("propertyId") String propertyId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return propertyService.getAvailableDates(propertyId, startDate, endDate);
    }

    @PostMapping
    public void initProperty(@RequestParam String propertyName) throws Exception {
        propertyService.initProperty(propertyName);
    }
}
