package org.tudelft.blockchain.booking.otawebapp.repository;

import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.model.DateInterval;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to get data either from the blockchain or from a storage.
 * This is a DUMMY implementation
 */
@Component
public class PropertyRepository {
    // TODO Dummy

    public Set<DateInterval> getAvailableDates(int propertyId) {
        return new HashSet<>();
    }

    public Set<DateInterval> getAvailableDates(int propertyId, DateInterval dateInterval) {
        return new HashSet<>();
    }
}
