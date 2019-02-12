package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity createProperty(@RequestParam("name") String propertyName) throws Exception {
        return ResponseEntity.ok(propertyService.createProperty("PropertyOwner", propertyName.toLowerCase()));
    }

    @PostMapping("/invite/{otaName}")
    public void inviteOta(@PathVariable("otaName") String otaName) {
        throw new NotImplementedException();
    }
}
