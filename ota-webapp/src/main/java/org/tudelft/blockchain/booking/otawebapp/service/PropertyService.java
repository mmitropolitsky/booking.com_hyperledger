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
        String chainCodeName = chainCodeService.installOverbookingChainCode(orgName, channel.getPeers());
        chainCodeService.instantiateChainCode(orgName, chainCodeName,
                channel, new String[0]);

    }

    public synchronized void joinProperty(String orgName, String propertyName) throws Exception {
        Channel channel = channelService.joinChannel(orgName, propertyName);
        chainCodeService.installOverbookingChainCode(orgName, channel.getPeers());
    }
}
