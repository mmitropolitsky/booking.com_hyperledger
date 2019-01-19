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

    @PostMapping("/channels")
    public void createChannel(@RequestParam String channelName) throws Exception {
        propertyService.createPropertyChannel("admin", "org1", "Org1MSP", "peer0.org1.example.com", "grpc://localhost:7051",
                "eventhub01", "grpc://localhost:7053", "orderer.example.com", "grpc://localhost:7050", channelName);
    }
}
