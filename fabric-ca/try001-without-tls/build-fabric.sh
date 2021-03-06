docker stop $(docker ps -aq)
docker rm -f $(docker ps -aq)
docker network prune
docker volume rm $(docker volume list)
rm -rf channel-artifacts/ crypto-config

cryptogen generate --config crypto-config.yaml
rm -rf ./crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/msp
cp -r ./client-guo-msps/org1.example.com/peer0/msp ./crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com
rm -rf ./crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/msp
cp -r ./client-guo-msps/org1.example.com/peer1/msp ./crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com
rm -rf ./crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/msp
cp -r ./client-guo-msps/org2.example.com/peer0/msp ./crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com
rm -rf ./crypto-config/peerOrganizations/org2.example.com/peers/peer1.org2.example.com/msp
cp -r ./client-guo-msps/org2.example.com/peer1/msp ./crypto-config/peerOrganizations/org2.example.com/peers/peer1.org2.example.com
rm -rf ./crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/msp
cp -r ./client-guo-msps/example.com/orderer/msp ./crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com

rm -rf ./crypto-config/ordererOrganizations/example.com/msp
cp -r ./client-guo-msps/example.com/msp ./crypto-config/ordererOrganizations/example.com
rm -rf ./crypto-config/peerOrganizations/org1.example.com/msp
cp -r ./client-guo-msps/org1.example.com/msp ./crypto-config/peerOrganizations/org1.example.com
rm -rf ./crypto-config/peerOrganizations/org2.example.com/msp
cp -r ./client-guo-msps/org2.example.com/msp ./crypto-config/peerOrganizations/org2.example.com

rm -rf ./crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
cp -r ./client-guo-msps/org1.example.com/admin/msp ./crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com
rm -rf ./crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
cp -r ./client-guo-msps/org2.example.com/admin/msp ./crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com

mkdir -p ./crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/admincerts
cp ./client-guo-msps/org1.example.com/admin/msp/signcerts/cert.pem ./crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/admincerts
mkdir -p ./crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/admincerts
cp ./client-guo-msps/org2.example.com/admin/msp/signcerts/cert.pem ./crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/admincerts

mkdir ./channel-artifacts
configtxgen -profile TwoOrgsOrdererGenesis -channelID byfn-sys-channel -outputBlock ./channel-artifacts/genesis.block
configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID mychannel
configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Org1MSPanchors.tx -channelID mychannel -asOrg Org1MSP
configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Org2MSPanchors.tx -channelID mychannel -asOrg Org2MSP
