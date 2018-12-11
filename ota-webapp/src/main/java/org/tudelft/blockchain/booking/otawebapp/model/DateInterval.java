package org.tudelft.blockchain.booking.otawebapp.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateInterval {
    LocalDate startDate;
    LocalDate endDate;
}
