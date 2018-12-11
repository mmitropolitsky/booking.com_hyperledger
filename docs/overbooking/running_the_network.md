# Creating the Overbooking Fabric Network

The domain structure will be :
* eitbooking : the "root" of the subdomains in the system
    * overbooking : where the OTAs and Partners interact for overbooking logic
    * transactions : where Trusted Third Parties (TTP) and OTAs can 'negotiate' Private Transaction Certificates

## Pre-requisites

1. [Install the pre-requisites](https://hyperledger-fabric.readthedocs.io/en/latest/prereqs.html)
2. [Install the samples, binaries and Docker images](https://hyperledger-fabric.readthedocs.io/en/latest/install.html) \
**IMPORTANT** : the docker combined docker images amount to ~16GB, the samples are ~160MB
3. Clone this repo

## Setup the environment

To generate the required certificates, we will need to execute the `cryptogen` binary provided in `fabric-samples`. To
do so easily you can add the samples `bin` folder to your PATH environment variable.

## Creating the network

We will need to generate certificates, private-public key pairs, etc... To do so :

1. navigate to `blockchain_booking/hyperledger` folder
2. Generate the **Orderer** and **Peer** organizations certificates :
    * execute `cryptogen generate --config=./crypto-config.yaml`
    * a new folder `crypto-config` is created, containing all the certificates needed for the organizations defined in
    `crypto-config.yaml`
3. Create the orderer's **genesis block** (first block of the chain) : \
`configtxgen -profile BasicOrdererGenesis -outputBlock ./channel-artifacts/genesis.block`
4. Create the **Channel Configuration Transaction** (an initial transaction that specifies the channel configuration settings) : \
`export CHANNEL_NAME=basic` \
`mkdir -p channel-artifacts` \
`configtxgen -profile BasicChannel -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID $CHANNEL_NAME`
5. Define OTA1's **anchor peer** on the channel : \
`configtxgen -profile BasicChannel -outputAnchorPeersUpdate ./channel-artifacts/Ota1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg OTA1`
6. Start the network : \
`export IMAGE_TAG=amd64-1.3.0` \
*Note* : if you need another target architecture than *amd64* or another version than 1.3.0, replace with the desired 
tag from [Hyperledger Docker Images Tags](https://hub.docker.com/r/hyperledger/fabric-orderer/tags/)
`export COMPOSE_PROJECT_NAME=Basic` \
`docker-compose -f docker-compose.yaml up -d`
