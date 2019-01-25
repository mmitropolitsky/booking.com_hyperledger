#!/bin/sh
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
export PATH=../bin:$PATH
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

mv ./crypto-config/peerOrganizations/org1.example.com/ca/*_sk ./crypto-config/peerOrganizations/org1.example.com/ca/org1_sk

mkdir -p ./crypto-config/peerOrganizations/idemixMSP1.example.com
idemixgen ca-keygen --output=./crypto-config/peerOrganizations/idemixMSP1.example.com
if [ "$?" -ne 0 ]; then
  echo "Failed to generate Org1Idemix crypto material..."
  exit 1
else
  idemixgen signerconfig -u Fabric -e 'johndoe' -r 1234 --output=./crypto-config/peerOrganizations/idemixMSP1.example.com
fi

mkdir -p ./config
# generate genesis block for orderer
configtxgen -profile OneOrgOrdererGenesis -outputBlock ./config/genesis.block
if [ "$?" -ne 0 ]; then
  echo "Failed to generate orderer genesis block..."
  exit 1
else
  configtxgen -inspectBlock ./config/genesis.block > genesis.block.json
fi

# generate channel configuration transaction
configtxgen -profile OneOrgChannel -outputCreateChannelTx ./config/channel.tx -channelID $CHANNEL_NAME
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
else
  configtxgen -inspectChannelCreateTx ./config/channel.tx > channelCreateTx.json
fi

# generate anchor peer transaction
configtxgen -profile OneOrgChannel -outputAnchorPeersUpdate ./config/Org1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Org1MSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for Org1MSP..."
  exit 1
fi
