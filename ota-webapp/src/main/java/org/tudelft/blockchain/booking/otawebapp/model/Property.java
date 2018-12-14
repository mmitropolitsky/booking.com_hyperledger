package org.tudelft.blockchain.booking.otawebapp.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;

import java.util.Collection;
import java.util.HashSet;

@Data
@RequiredArgsConstructor
public class Property {
    @NonNull
    private String id;
    @NonNull
    private String address;
    @NonNull
    private String description;

    private Collection<DateAvailabilityPair> availableIntervals = new HashSet<>();
}
