#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
# Exit on first error, print all commands.
set -ev

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

docker-compose -f docker-compose.yml down

docker-compose -f docker-compose.yml up -d

# wait for Hyperledger Fabric to start
# incase of errors when running later commands, issue export FABRIC_START_TIMEOUT=<larger number>
export FABRIC_START_TIMEOUT=10
#echo ${FABRIC_START_TIMEOUT}
#sleep ${FABRIC_START_TIMEOUT}

# Create the channel
docker exec -e "CORE_PEER_LOCALMSPID=PropertyOwnerMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@property-owner.tudelft.org/msp" peer0.property-owner.tudelft.org peer channel create -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/mychannel.tx

# Join peer0.property-owner.tudelft.org to the channel.
docker exec -e "CORE_PEER_LOCALMSPID=PropertyOwnerMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@property-owner.tudelft.org/msp" peer0.property-owner.tudelft.org peer channel join -b mychannel.block

# Fetch config of mychannel in peer0.ota-a.tudelft.org.
docker exec -e "CORE_PEER_LOCALMSPID=OtaAMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-a.tudelft.org/msp" peer0.ota-a.tudelft.org peer channel fetch 0 mychannel.block -c mychannel --orderer orderer.example.com:7050

# Join peer0.ota-a.tudelft.org to the channel.
docker exec -e "CORE_PEER_LOCALMSPID=OtaAMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-a.tudelft.org/msp" peer0.ota-a.tudelft.org peer channel join -b mychannel.block

# Fetch config of mychannel in peer0.ota-b.tudelft.org.
docker exec -e "CORE_PEER_LOCALMSPID=OtaBMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-b.tudelft.org/msp" peer0.ota-b.tudelft.org peer channel fetch 0 mychannel.block -c mychannel --orderer orderer.example.com:7050

# Join peer0.ota-b.tudelft.org to the channel.
docker exec -e "CORE_PEER_LOCALMSPID=OtaBMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-b.tudelft.org/msp" peer0.ota-b.tudelft.org peer channel join -b mychannel.block


Update the channel definition to define the anchor peer for Org1 as peer0.property-owner.tudelft.org
docker exec -e "CORE_PEER_LOCALMSPID=PropertyOwnerMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@property-owner.tudelft.org/msp" peer0.property-owner.tudelft.org peer channel update -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/PropertyOwnerMSPanchors.tx

# Update the channel definition to define the anchor peer for Org1 as peer0.ota-a.tudelft.org
docker exec -e "CORE_PEER_LOCALMSPID=OtaAMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-a.tudelft.org/msp" peer0.ota-a.tudelft.org peer channel update -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/OtaAMSPanchors.tx

# Update the channel definition to define the anchor peer for Org1 as peer0.ota-b.tudelft.org
docker exec -e "CORE_PEER_LOCALMSPID=OtaBMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-b.tudelft.org/msp" peer0.ota-b.tudelft.org peer channel update -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/OtaBMSPanchors.tx

# Install chaincode on property owner peer
docker exec -e "CORE_PEER_LOCALMSPID=PropertyOwnerMSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/property-owner.tudelft.org/users/Admin@property-owner.tudelft.org/msp" cli peer chaincode install -n overbooking -v 1.0 -p /opt/gopath/src/github.com/chaincode/ -l java
# Instantiate the chaincode on the channel
docker exec -e "CORE_PEER_LOCALMSPID=PropertyOwnerMSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/property-owner.tudelft.org/users/Admin@property-owner.tudelft.org/msp" cli peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n overbooking -l java -v 1.0 -c '{"Args":["init"]}' -P "OR ('PropertyOwnerMSP.member','OtaAMSP.member', 'OtaBMSP.member')"

# Install chaincode on peer0.ota-a.tudelft.org peer
docker exec -e "CORE_PEER_ADDRESS=peer0.ota-a.tudelft.org:7051" -e "CORE_PEER_LOCALMSPID=OtaAMSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/ota-a.tudelft.org/users/Admin@ota-a.tudelft.org/msp" cli peer chaincode install -n overbooking -v 1.0 -p /opt/gopath/src/github.com/chaincode/ -l java
# Install chaincode on peer0.ota-b.tudelft.org peer
docker exec -e "CORE_PEER_ADDRESS=peer0.ota-b.tudelft.org:7051" -e "CORE_PEER_LOCALMSPID=OtaBMSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/ota-b.tudelft.org/users/Admin@ota-b.tudelft.org/msp" cli peer chaincode install -n overbooking -v 1.0 -p /opt/gopath/src/github.com/chaincode/ -l java

sleep 10

# Query chaincode from peer peer0.property-owner.tudelft.org
docker exec -e "CORE_PEER_LOCALMSPID=PropertyOwnerMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@property-owner.tudelft.org/msp" peer0.property-owner.tudelft.org peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n overbooking -c '{"function":"isBookable","Args":["2019-05-01","2019-05-10"]}'

# Query chaincode from peer peer0.ota-a.tudelft.org
docker exec -e "CORE_PEER_LOCALMSPID=OtaAMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-a.tudelft.org/msp" peer0.ota-a.tudelft.org peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n overbooking -c '{"function":"isBookable","Args":["2019-05-01","2019-05-10"]}'

# Query chaincode from peer peer0.ota-b.tudelft.org
docker exec -e "CORE_PEER_LOCALMSPID=OtaBMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@ota-b.tudelft.org/msp" peer0.ota-b.tudelft.org peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n overbooking -c '{"function":"isBookable","Args":["2019-05-01","2019-05-10"]}'