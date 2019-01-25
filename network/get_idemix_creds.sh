#!/bin/sh
TEST_DIR=$HOME/test
LOCAL_MSPID=idemixMSPID1

mkdir -p $TEST_DIR
rm -rf $TEST_DIR/*
docker run --init --name enroller --network=net_basic hyperledger/fabric-ca sh -c "fabric-ca-client enroll -u http://admin:adminpw@ca.example.com:7054; export USER_SECRET=$(fabric-ca-client register --id.name user1 -u http://ca.example.com:7054 | cut -d ' ' -f 2); fabric-ca-client enroll --enrollment.type idemix -u http://user1:$USER_SECRET@ca.example.com:7054"

docker cp enroller:/etc/hyperledger/fabric-ca-server/msp $TEST_DIR/
mv $TEST_DIR/msp/IssuerRevocationPublicKey $TEST_DIR/msp/RevocationPublicKey
docker cp $TEST_DIR/msp peer0.org1.example.com:/tmp/
docker cp ./crypto-config/peerOrganizations/idemixMSP1.example.com/user peer0.org1.example.com:/tmp/

docker exec -e "CORE_PEER_LOCALMSPID=$LOCAL_MSPID" -e "CORE_PEER_LOCALMSPTYPE=idemix" -e "CORE_PEER_MSPCONFIGPATH=/tmp" peer0.org1.example.com peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n overbooking -c '{"function": "isBookable", "Args": ["2019-02-01","2019-02-05"]}'