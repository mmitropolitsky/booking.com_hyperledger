package org.tudelft.blockchain.booking.otawebapp.model;

import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

@Data
public class Property {
    private Integer id;
    private String address;
    private String description;
    private Collection<DateInterval> availableIntervals = new HashSet<>();

}
