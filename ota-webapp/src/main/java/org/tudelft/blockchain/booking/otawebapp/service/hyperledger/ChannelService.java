package org.tudelft.blockchain.booking.otawebapp.service.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tudelft.blockchain.booking.otawebapp.service.FabricClientService;
import org.tudelft.blockchain.booking.otawebapp.service.OrganizationCredentialService;
import org.tudelft.blockchain.booking.otawebapp.util.OrgStringBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;

@Service
public class ChannelService {


    @Autowired
    FabricClientService fabricClientService;

    @Autowired
    ChannelService channelConfigurationService;

    @Autowired
    OrganizationCredentialService organizationCredentialService;

    @Autowired
    OrgStringBuilder orgStringBuilder;

    @Autowired
    ChainCodeService chainCodeService;

    @Value("${org.tudelft.blockchain.booking.channel.configuration.path}")
    String configPath;

    private static final String CHANNEL_CONFIG_PROFILE = "ThreeOrgChannel";

    public Channel createChannel(String orgName, String channelName) throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();

        Orderer orderer = client.newOrderer(orgStringBuilder.getOrdererName(), orgStringBuilder.getOrdererUrl());
        ChannelConfiguration channelConfiguration = channelConfigurationService.createChannelConfiguration(channelName);
        Channel newChannel = client.newChannel(channelName, orderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, admin));

        initializeChannel(orgName, newChannel);

        return newChannel;
    }

    public Channel getChannel(String orgName, String channelName) throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();
        Channel channel = client.getChannel(channelName);
        if (channel == null) {
            channel = client.newChannel(channelName);
        }
        Peer peer = client.newPeer(orgStringBuilder.getPeerName(orgName, 0), orgStringBuilder.getPeerUrl(orgName));
        EventHub eventHub = client.newEventHub(orgStringBuilder.getEventHubName(), orgStringBuilder.getEventHubUrl(orgName));
        Orderer orderer = client.newOrderer(orgStringBuilder.getOrdererName(), orgStringBuilder.getOrdererUrl());
        channel.addEventHub(eventHub);
        channel.addOrderer(orderer);
        channel.addPeer(peer);
        channel.initialize();
        return channel;
    }

    public void joinChannel(String orgName, String channelName) throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();

        Channel channel = client.getChannel(channelName);
        if (channel == null) {
            channel = client.newChannel(channelName);
        }
        channel = initializeChannel(orgName, channel);
        chainCodeService.installOverbookingChainCode(orgName, channelName, channel);

    }

    private Channel initializeChannel(String orgName, Channel channel) throws Exception {
        try {
//            User admin = organizationCredentialService.getOrgAdmin(orgName);
//            fabricClientService.changeContext(admin);
            HFClient client = fabricClientService.getClient();

            Peer peer = client.newPeer(orgStringBuilder.getPeerName(orgName, 0), orgStringBuilder.getPeerUrl(orgName));
            EventHub eventHub = client.newEventHub(orgStringBuilder.getEventHubName(), orgStringBuilder.getEventHubUrl(orgName));
            Orderer orderer = client.newOrderer(orgStringBuilder.getOrdererName(), orgStringBuilder.getOrdererUrl());
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.joinPeer(peer, createPeerOptions()); //Default is all roles.
            channel.initialize();

            // TODO change to user context?
//            networkService.changeToUserContext();

            return channel;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public ChannelConfiguration createChannelConfiguration(String channelName) throws Exception {
        try {
            String channelConfigurationOutputPath = configPath + File.separator + channelName + ".tx";
            String command = "/home/milko/Projects/fabric-samples/bin/configtxgen -profile " + CHANNEL_CONFIG_PROFILE +
                    " -outputCreateChannelTx " + channelConfigurationOutputPath +
                    " -channelID " + channelName;
            System.out.println(command);
            executeCommand(command);

            return new ChannelConfiguration(new File(channelConfigurationOutputPath));
        } catch (IOException | InvalidArgumentException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // TODO command line service
    private String executeCommand(String command) {
        Runtime rt = Runtime.getRuntime();
        StringBuilder response = new StringBuilder();
        try {
            Process process = rt.exec(new String[] {"bash", "-c", command});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

}