package org.tudelft.blockchain.booking.otawebapp.service;

import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;
import org.tudelft.blockchain.booking.otawebapp.repository.PropertyRepository;

import java.time.LocalDate;
import java.util.Collection;

@Component
public class PropertyService {

    private PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }


    public Collection<DateAvailabilityPair> getAvailableDates(String propertyId, LocalDate startDate, LocalDate endDate) {
        return propertyRepository.getAvailableDates(propertyId, startDate, endDate);
    }

    public void createPropertyChannel(String adminUsername, String affiliation, String mspId, String peerName, String peerUrl, String eventHubName, String eventHubUrl,
                                      String ordererName, String ordererUrl, String channelName) throws Exception {
        propertyRepository.createPropertyChannel(adminUsername, affiliation, mspId, peerName, peerUrl, eventHubName, eventHubUrl, ordererName, ordererUrl, channelName);
    }

}
