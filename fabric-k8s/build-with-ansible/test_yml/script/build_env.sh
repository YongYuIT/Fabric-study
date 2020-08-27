docker stop $(docker ps -a | grep hyperledger | awk '{print $1}')
docker rm -f $(docker ps -a | grep hyperledger | awk '{print $1}')
docker network prune
docker volume rm $(docker volume list | awk '{print $2}')

rm -rf channel-artifacts/ configtx.yaml crypto-config/
cryptogen generate --config=test_yml/crypto-config-peer.yaml
cryptogen generate --config=test_yml/crypto-config-orderer.yaml

mkdir channel-artifacts
ln -s test_yml/configtx-genesis.yaml configtx.yaml
configtxgen -profile OrdererGenesis -channelID syschannel -outputBlock channel-artifacts/genesis.block

rm configtx.yaml
ln -s test_yml/configtx-channel.yaml configtx.yaml
configtxgen -profile Channel -channelID demochannel -outputCreateChannelTx channel-artifacts/channel.tx
configtxgen -profile Channel -channelID demochannel -outputAnchorPeersUpdate ./channel-artifacts/Org1MSPanchors.tx -asOrg Org1MSP
configtxgen -profile Channel -channelID demochannel -outputAnchorPeersUpdate ./channel-artifacts/Org2MSPanchors.tx -asOrg Org2MSP
configtxgen -profile Channel -channelID demochannel -outputAnchorPeersUpdate ./channel-artifacts/Org3MSPanchors.tx -asOrg Org3MSP

docker-compose -f test_yml/docker-compose-orderer.yaml up -d
docker-compose -f test_yml/docker-compose-peer0.org1.example.com.yaml up -d
docker-compose -f test_yml/docker-compose-peer0.org2.example.com.yaml up -d
docker-compose -f test_yml/docker-compose-peer0.org3.example.com.yaml up -d
docker-compose -f test_yml/docker-compose-cli.yaml up -d

docker exec -it cli /bin/bash