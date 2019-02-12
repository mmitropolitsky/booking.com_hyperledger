package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.otawebapp.service.hyperledger.ChainCodeService;
import org.tudelft.blockchain.booking.otawebapp.service.hyperledger.ChannelService;

@Component
public class PropertyService {

    private final ChainCodeService chainCodeService;

    private final ChannelService channelService;

    @Autowired
    public PropertyService(ChainCodeService chainCodeService, ChannelService channelService) {
        this.chainCodeService = chainCodeService;
        this.channelService = channelService;
    }

    public String createProperty(String orgName, String propertyName) throws Exception {

        // TODO add db entry?
        Channel channel = channelService.createChannel(orgName, propertyName);
        String chainCodeName = chainCodeService.installOverbookingChainCode(orgName, channel.getPeers());
        return chainCodeService.instantiateChainCode(orgName, chainCodeName,
                channel, new String[0]);
    }

    public synchronized String joinProperty(String orgName, String propertyName) {
        try {
            Channel channel = channelService.joinChannel(orgName, propertyName);
            chainCodeService.installOverbookingChainCode(orgName, channel.getPeers());
            return "Successfully joined channel " + propertyName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error:" + e.getMessage();
        }


    }
}
