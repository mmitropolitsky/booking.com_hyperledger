BIN_DIR=bin
CGEN=$(BIN_DIR)/cryptogen
TXGEN=$(BIN_DIR)/configtxgen

CRYPTO_CFG=crypto-config.yaml
TX_CFG=configtx.yaml

CH_DIR=channel-artifacts
CH_NAME=eit

.PHONY: all clean

all: crypto genesis channel anchor

crypto: $(CRYPTO_CFG)
	$(CGEN) generate --config=$(CRYPTO_CFG)

genesis: crypto $(TX_CFG)
	mkdir -p $(CH_DIR)
	$(TXGEN) -profile TwoOrgsOrdererGenesis -outputBlock ./$(CH_DIR)/genesis.block -channelID syschan

channel: genesis
	$(TXGEN) -profile TwoOrgsChannel -outputCreateChannelTx ./$(CH_DIR)/channel.tx -channelID $(CH_NAME)

anchor: channel
	$(TXGEN) -profile TwoOrgsChannel -outputAnchorPeersUpdate ./$(CH_DIR)/Org1MSPanchors.tx -channelID $(CH_NAME) -asOrg Ota1MSP
	#$(TXGEN) -profile TwoOrgsChannel -outputAnchorPeersUpdate ./$(CH_DIR)/Org2MSPanchors.tx -channelID $(CH_NAME) -asOrg Org2MSP

clean:
	rm -rf $(CH_DIR) crypto-config
