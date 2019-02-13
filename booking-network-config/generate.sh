#!/bin/sh
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
export PATH=$GOPATH/src/github.com/hyperledger/fabric/build/bin:${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=${PWD}
CHANNEL_NAME=mychannel

# remove previous crypto material and config transactions
rm -fr config/*
rm -fr crypto-config/*

# generate crypto material
cryptogen generate --config=./crypto-config.yaml
if [ "$?" -ne 0 ]; then
  echo "Failed to generate crypto material..."
  exit 1
fi

mv ./crypto-config/peerOrganizations/property-owner.tudelft.org/ca/*_sk ./crypto-config/peerOrganizations/property-owner.tudelft.org/ca/property_owner_sk
mv ./crypto-config/peerOrganizations/ota-a.tudelft.org/ca/*_sk ./crypto-config/peerOrganizations/ota-a.tudelft.org/ca/ota_a_sk
mv ./crypto-config/peerOrganizations/ota-b.tudelft.org/ca/*_sk ./crypto-config/peerOrganizations/ota-b.tudelft.org/ca/ota_b_sk

# generate genesis block for orderer
configtxgen -profile ThreeOrgOrdererGenesis -outputBlock ./config/genesis.block
if [ "$?" -ne 0 ]; then
  echo "Failed to generate orderer genesis block..."
  exit 1
fi

# generate channel configuration transaction
configtxgen -profile ThreeOrgChannel -outputCreateChannelTx ./config/mychannel.tx -channelID $CHANNEL_NAME
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
fi

# generate anchor peer transaction
configtxgen -profile ThreeOrgChannel -outputAnchorPeersUpdate ./config/PropertyOwnerMSPanchors.tx -channelID $CHANNEL_NAME -asOrg PropertyOwnerMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for PropertyOwnerMSP..."
  exit 1
fi

# generate OTA A anchor peer transaction
configtxgen -profile ThreeOrgChannel -outputAnchorPeersUpdate ./config/OtaAMSPanchors.tx -channelID $CHANNEL_NAME -asOrg OtaAMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for OtaAMSP..."
  exit 1
fi

# generate OTA B anchor peer transaction
configtxgen -profile ThreeOrgChannel -outputAnchorPeersUpdate ./config/OtaBMSPanchors.tx -channelID $CHANNEL_NAME -asOrg OtaBMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for OtaBMSP..."
  exit 1
fi
