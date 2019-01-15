package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.tudelft.blockchain.booking.otawebapp.model.Property;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.service.ChainCodeService;
import org.tudelft.blockchain.booking.otawebapp.service.CredentialService;
import org.tudelft.blockchain.booking.otawebapp.service.PropertyService;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private PropertyService propertyService;

    @Autowired
    private ChainCodeService chainCodeService;

    @Autowired
    private CredentialService credentialService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public boolean getAllProperties() {
        try {
//            chainCodeService.createChannel();
//            chainCodeService.installChainCode();

                String caURL = "http://localhost:7054";
                HFUser admin = credentialService.adminUser(caURL);
                System.out.println("name : " + admin.getName());
                System.out.println("roles : " + admin.getRoles());
                System.out.println("account : " + admin.getAccount());
                System.out.println("MSP ID : " + admin.getMspId());

                IdemixEnrollment idemixEnrollment = credentialService.idemixEnroll(admin, caURL);
                return idemixEnrollment != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    @GetMapping("/{propertyId}")
//    public Property getPropertyById(@PathVariable("propertyId") String propertyId) {
//        return new Property(propertyId, "address", "desc");
//    }

    @GetMapping("/{propertyId}/availability")
    public Collection<DateAvailabilityPair> getAvailability(
            @PathVariable("propertyId") String propertyId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return propertyService.getAvailableDates(propertyId, startDate, endDate);
    }
}
