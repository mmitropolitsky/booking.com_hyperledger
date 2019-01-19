package org.tudelft.blockchain.booking.otawebapp.repository;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tudelft.blockchain.booking.otawebapp.model.Property;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.AvailabilityStatus;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.DateAvailabilityPair;
import org.tudelft.blockchain.booking.otawebapp.model.hyperledger.HFUser;
import org.tudelft.blockchain.booking.otawebapp.repository.hyperledger.FabricRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Used to get data either from the blockchain or from a storage.
 * This is a DUMMY implementation
 */
@Repository
public class PropertyRepository {

    @Autowired
    private FabricRepository fabricRepository;

    private List<Property> dummyPropertyList = new ArrayList<>();

    // TODO Dummy
    public PropertyRepository() {
        for (int i = 0; i < 10; i++) {
            dummyPropertyList.add(new Property("id" + i, "address" + i, "description" + i));
        }
    }

    public Collection<Property> getAllProperties() {
        return new ArrayList<>();
    }

    public Property getPropertyById(String propertyId) {
        return null;
    }

    /**
     * What is returned by the blockchain.
     *
     * @param propertyId
     * @param startDate
     * @param endDate
     * @return
     */
    public Collection<DateAvailabilityPair> getAvailableDates(String propertyId, LocalDate startDate, LocalDate endDate) {
        List<DateAvailabilityPair> dateAvailabilityPairs = new ArrayList<>();
        dateAvailabilityPairs
                .add(new DateAvailabilityPair(LocalDate.now(), AvailabilityStatus.AVAILABLE));
        dateAvailabilityPairs
                .add(new DateAvailabilityPair(LocalDate.now().plusDays(1), AvailabilityStatus.BOOKED));
        dateAvailabilityPairs
                .add(new DateAvailabilityPair(LocalDate.now().plusDays(2), AvailabilityStatus.AVAILABLE));
        return dateAvailabilityPairs;

    }

    public Channel createPropertyChannel(HFClient adminClient, HFUser admin, String peerName, String peerUrl, String eventHubName, String eventHubUrl,
                                         String ordererName, String ordererUrl, String channelName) throws Exception {
        return fabricRepository.createChannel(adminClient, admin, peerName, peerUrl, eventHubName, eventHubUrl, ordererName, ordererUrl, channelName);
    }
}
