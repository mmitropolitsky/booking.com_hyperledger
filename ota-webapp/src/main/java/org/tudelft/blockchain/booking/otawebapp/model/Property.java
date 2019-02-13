package org.tudelft.blockchain.booking.otawebapp.model;

import lombok.*;

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
}
