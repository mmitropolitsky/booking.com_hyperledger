package org.tudelft.blockchain.booking.otawebapp.service.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.AccessException;
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


    private final FabricClientService fabricClientService;

    private final OrganizationCredentialService organizationCredentialService;

    private final OrgStringBuilder orgStringBuilder;

    @Value("${org.tudelft.blockchain.booking.channel.configuration.path}")
    String configPath;

    @Value("${org.tudelft.blockchain.booking.configtxgen}")
    String configtxCommandPath;

    private static final String CHANNEL_CONFIG_PROFILE = "ThreeOrgChannel";

    @Autowired
    public ChannelService(FabricClientService fabricClientService,
                          OrganizationCredentialService organizationCredentialService, OrgStringBuilder orgStringBuilder) {
        this.fabricClientService = fabricClientService;
        this.organizationCredentialService = organizationCredentialService;
        this.orgStringBuilder = orgStringBuilder;
    }

    public Channel createChannel(String orgName, String channelName) throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();

        Orderer orderer = client.newOrderer(orgStringBuilder.getOrdererName(), orgStringBuilder.getOrdererUrl());
        ChannelConfiguration channelConfiguration = this.createChannelConfiguration(channelName);
        Channel newChannel = client.newChannel(channelName, orderer, channelConfiguration,
                client.getChannelConfigurationSignature(channelConfiguration, admin));

        joinChannel(orgName, newChannel);

        return newChannel;
    }

    private Channel joinChannel(String orgName, Channel channel) throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();

        addOrdererAndEventHubToChannel(client, orgName, channel);

        joinPeer(client, orgName, channel);

        channel.initialize();

        return channel;
    }

    public Channel joinChannel(String orgName, String channelName) throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();
        Channel channel = client.getChannel(channelName);
        if (channel == null) {
            channel = client.newChannel(channelName);
        }
        return joinChannel(orgName, channel);
    }

    public Channel getChannel(String orgName, String channelName) throws Exception {
        User admin = organizationCredentialService.getOrgAdmin(orgName);
        fabricClientService.changeContext(admin);
        HFClient client = fabricClientService.getClient();
        Peer peer = client.newPeer(orgStringBuilder.getPeerName(orgName, 0), orgStringBuilder.getPeerUrl(orgName));


        if (client.queryChannels(peer).contains(channelName)) {
            Channel channel = client.getChannel(channelName);
            if (channel == null) {
                channel = client.newChannel(channelName);
            }
            channel.addPeer(peer);


            addOrdererAndEventHubToChannel(client, orgName, channel);

            channel.initialize();
            return channel;
        } else {
            throw new AccessException("Organization [" + orgName + "] does not have access to property [" + channelName + "]");
        }
    }

    private ChannelConfiguration createChannelConfiguration(String channelName) throws Exception {
        try {
            String channelConfigurationOutputPath = configPath + File.separator + channelName + ".tx";
            String command = configtxCommandPath + " -profile " + CHANNEL_CONFIG_PROFILE +
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
            Process process = rt.exec(new String[]{"bash", "-c", command});
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

    private void joinPeer(HFClient client, String orgName, Channel channel) throws ProposalException, InvalidArgumentException {
        Peer peer = client.newPeer(orgStringBuilder.getPeerName(orgName, 0), orgStringBuilder.getPeerUrl(orgName));
        channel.joinPeer(peer, createPeerOptions()); //Default is all roles.
    }

    private void addOrdererAndEventHubToChannel(HFClient client, String orgName, Channel channel) throws InvalidArgumentException {
        EventHub eventHub = client.newEventHub(orgStringBuilder.getEventHubName(), orgStringBuilder.getEventHubUrl(orgName));
        Orderer orderer = client.newOrderer(orgStringBuilder.getOrdererName(), orgStringBuilder.getOrdererUrl());
        channel.addEventHub(eventHub);
        channel.addOrderer(orderer);
    }
}
