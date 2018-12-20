package org.tudelft.blockchain.booking.otawebapp.model;

import lombok.*;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;

import java.util.Collection;
import java.util.HashSet;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Property {
    @NonNull
    private String id;
    @NonNull
    private String address;
    @NonNull
    private String description;

    @EqualsAndHashCode.Exclude
    private Collection<DateAvailabilityPair> availableIntervals = new HashSet<>();


}
