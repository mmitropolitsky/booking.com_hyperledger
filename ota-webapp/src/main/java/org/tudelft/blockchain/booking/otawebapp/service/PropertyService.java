package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.service.hyperledger.ChainCodeService;
import org.tudelft.blockchain.booking.otawebapp.service.hyperledger.ChannelService;

@Component
public class PropertyService {

    @Autowired
    private ChainCodeService chainCodeService;

    @Autowired
    private ChannelService channelService;

    public void createProperty(String orgName, String propertyName) throws Exception {

        // TODO add db entry?
        Channel channel = channelService.createChannel(orgName, propertyName);
        String chainCodeName = chainCodeService.installOverbookingChainCode(orgName, propertyName, channel);
        chainCodeService.instantiateChainCode(orgName, propertyName + "Overbooking",
                channel, new String[0]);

    }

    public void joinProperty(String orgName, String propertyName) throws Exception {
        channelService.joinChannel(orgName, propertyName);
    }


//    public Collection<DateAvailabilityPair> getAvailableDates(String propertyId, LocalDate startDate, LocalDate endDate) {
//        return propertyRepository.getAvailableDates(propertyId, startDate, endDate);
//    }
}
