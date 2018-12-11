package org.tudelft.blockchain.booking.otawebapp.service;

import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.DateInterval;
import org.tudelft.blockchain.booking.otawebapp.repository.PropertyRepository;

import java.time.LocalDate;
import java.util.Set;

@Component
public class PropertyService {

    private PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Set<DateInterval> getAvailableDates(int propertyId) {
        return propertyRepository.getAvailableDates(propertyId);
    }

    public Set<DateInterval> getAvailableDates(int propertyId, DateInterval dateInterval) {
        return propertyRepository.getAvailableDates(propertyId);
    }

}
