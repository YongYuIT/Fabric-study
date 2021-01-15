docker stop $(docker ps -aq)
docker rm -f $(docker ps -aq)
docker network prune
docker volume rm $(docker volume list)
rm -rf channel-artifacts/ crypto-config

mkdir -p ./client-guo-msps/org1.example.com/admin/msp/admincerts
cp ./client-guo-msps/org1.example.com/admin/msp/signcerts/cert.pem ./client-guo-msps/org1.example.com/admin/msp/admincerts
mkdir -p ./client-guo-msps/org2.example.com/admin/msp/admincerts
cp ./client-guo-msps/org2.example.com/admin/msp/signcerts/cert.pem ./client-guo-msps/org2.example.com/admin/msp/admincerts

mkdir ./channel-artifacts
configtxgen -profile TwoOrgsOrdererGenesis -channelID byfn-sys-channel -outputBlock ./channel-artifacts/genesis.block
configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID mychannel
configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Org1MSPanchors.tx -channelID mychannel -asOrg Org1MSP
configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Org2MSPanchors.tx -channelID mychannel -asOrg Org2MSP
