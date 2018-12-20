package org.tudelft.blockchain.booking.otawebapp.model.hyperledger;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DateAvailabilityPair {
    private LocalDate date;
    private AvailabilityStatus status;
}
