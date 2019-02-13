#!/bin/sh
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
export PATH=$GOPATH/src/github.com/hyperledger/fabric/build/bin:${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=${PWD}
CHANNEL_NAME=property0

# generate genesis block for orderer
configtxgen -profile PropertyOwnerOrdererGenesis -outputBlock ./config/genesis-property-owner.block
if [ "$?" -ne 0 ]; then
  echo "Failed to generate orderer genesis block..."
  exit 1
fi

# generate channel configuration transaction
configtxgen -profile PropertyOwnerChannel -outputCreateChannelTx ./config/property-owner.tx -channelID propertytest
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
fi

# generate anchor peer transaction
configtxgen -profile PropertyOwnerChannel -outputAnchorPeersUpdate ./config/SinglePropertyOwnerMSPanchors.tx -channelID propertytest -asOrg PropertyOwnerMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for PropertyOwnerMSP..."
  exit 1
fi
