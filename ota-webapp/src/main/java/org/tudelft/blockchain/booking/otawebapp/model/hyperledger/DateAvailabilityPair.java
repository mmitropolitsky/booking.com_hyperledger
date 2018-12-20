package org.tudelft.blockchain.booking.otawebapp.model.hyperledger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateAvailabilityPair {
    private LocalDate date;
    private AvailabilityStatus status;
}
