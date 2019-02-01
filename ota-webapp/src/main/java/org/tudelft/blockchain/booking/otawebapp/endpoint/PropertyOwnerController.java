package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.springframework.web.bind.annotation.*;
import org.tudelft.blockchain.booking.otawebapp.service.PropertyService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@RestController
@RequestMapping("/api/po")
public class PropertyOwnerController {

    private PropertyService propertyService;

    public PropertyOwnerController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/property")
    public void createProperty(@RequestParam("name") String propertyName) throws Exception {
        propertyService.createProperty("PropertyOwner", propertyName.toLowerCase());
    }

    @PostMapping("/invite/{otaName}")
    public void inviteOta(@PathVariable("otaName") String otaName) {
        throw new NotImplementedException();
    }
}
