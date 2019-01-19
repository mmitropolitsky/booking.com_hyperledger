package org.tudelft.blockchain.booking.otawebapp.service;

import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ChannelConfigurationService {

    @Value("org.tudelft.blockchain.booking.channel.configuration.path")
    private String channelConfigurationPath;

    public ChannelConfiguration createChannelConfiguration(String channelName) throws Exception {
        try {
            String path = channelConfigurationPath + channelName + ".tx";
            String command = "configtxgen -profile OneOrgChannel -outputCreateChannelTx /home/viktoriya/Projects/fabric-samples/basic-network/config/" + channelName + ".tx -channelID " + channelName;
            System.out.println(command);
            String response = executeCommand(command);

            return new ChannelConfiguration(new File(path));
        } catch (IOException | InvalidArgumentException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String executeCommand(String command) {
        Runtime rt = Runtime.getRuntime();
        String response = "";
        try {
            Process process = rt.exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

}
