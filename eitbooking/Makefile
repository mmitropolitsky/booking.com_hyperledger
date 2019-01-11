BIN_DIR=bin
CGEN=$(BIN_DIR)/cryptogen
TXGEN=$(BIN_DIR)/configtxgen

CRYPTO_CFG=crypto-config.yaml
TX_CFG=configtx.yaml

CHANNEL_DIR=channel-artifacts
CHANNEL_NAME=eit

CHAINCODE_DIR=/var/hyperledger/chaincode/
CHAINCODE_NAME=overbooking

ORDERER=orderer.eitbooking.overbooking.com
ORDERER_PORT=7050

CLI_PEER=docker exec cli peer

.PHONY: all clean run installcc instantiatecc stop

all: anchor

crypto: $(CRYPTO_CFG)
	$(CGEN) generate --config=$(CRYPTO_CFG)

genesis: crypto $(TX_CFG)
	mkdir -p $(CHANNEL_DIR)
	$(TXGEN) -profile TwoOrgsOrdererGenesis -outputBlock ./$(CHANNEL_DIR)/genesis.block -channelID $(CHANNEL_NAME)

channel: genesis
	$(TXGEN) -profile TwoOrgsChannel -outputCreateChannelTx ./$(CHANNEL_DIR)/channel.tx -channelID $(CHANNEL_NAME)

anchor: channel
	$(TXGEN) -profile TwoOrgsChannel -outputAnchorPeersUpdate ./$(CHANNEL_DIR)/Org1MSPanchors.tx -channelID $(CHANNEL_NAME) -asOrg Ota1MSP
	#$(TXGEN) -profile TwoOrgsChannel -outputAnchorPeersUpdate ./$(CHANNEL_DIR)/Org2MSPanchors.tx -channelID $(CHANNEL_NAME) -asOrg Org2MSP

run: anchor
	docker-compose -f docker-compose-cli.yaml up &

createchannel:
	$(CLI_PEER) channel create -o $(ORDERER):$(ORDERER_PORT) -c $(CHANNEL_NAME) -f $(CHANNEL_DIR)/channel.tx --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/eitbooking.overbooking.com/orderers/$(ORDERER)/msp/tlscacerts/tlsca.eitbooking.overbooking.com-cert.pem

joinchannel:
	$(CLI_PEER) channel join -b basic.block

installcc:
	$(CLI_PEER) chaincode install -l java -n $(CHAINCODE_NAME) -v v0 -p $(CHAINCODE_DIR)/$(CHAINCODE_NAME)

instantiatecc:
	$(CLI_PEER) chaincode instantiate -o $(ORDERER):$(ORDERER_PORT) -C $(CHANNEL_NAME) -l java -n $(CHAINCODE_NAME) -v v0 -c '{"Args":["init"]}' -P 'OR ("Ota1MSP.member")'

stop:
	docker-compose -f docker-compose-cli.yaml down

clean:
	rm -rf $(CHANNEL_DIR) crypto-config scripts ../chaincode
