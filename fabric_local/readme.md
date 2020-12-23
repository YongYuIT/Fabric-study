* ref to hello-fabric2.0/build-netrowk/test1-build-with-docker-compose
# Gen MSP files
## copy files
* copy crypto-config-orderer.yaml 
* crypto-config-peer.yaml 
* configtx_firstblock.yaml
* configtx_channel.yaml
from "test1-build-with-docker-compose"
## gen msp config files
~~~shell script
$ cd local_try
$ cryptogen generate --config crypto-config-orderer.yaml
$ cryptogen generate --config crypto-config-peer.yaml
~~~
## gen gen-block & channel config files
~~~shell script
$ mkdir channel-artifacts
$ ln -s configtx_firstblock.yaml configtx.yaml
$ configtxgen -profile SampleMultiNodeEtcdRaft -channelID thk-sys-channel -outputBlock channel-artifacts/genesis.block

$ rm configtx.yaml
$ ln -s configtx_channel.yaml configtx.yaml
$ configtxgen -profile THKChannel -outputCreateChannelTx channel-artifacts/channel.tx -channelID thkchannel
~~~
## gen anchor update files
~~~shell script
$ configtxgen -profile THKChannel -outputAnchorPeersUpdate channel-artifacts/THKOrg1MSPanchors.tx -channelID thkchannel -asOrg THKOrg1
$ configtxgen -profile THKChannel -outputAnchorPeersUpdate channel-artifacts/THKOrg2MSPanchors.tx -channelID thkchannel -asOrg THKOrg2
~~~
# Start orderer with docker
## copy order docker file 
remove network config and start orderer
~~~shell script
$ docker-compose -f docker_comp_orderer.yaml up -d
~~~
# Start org1 peers with docker
~~~shell script
$ docker-compose -f docker_comp_thk_org1.yaml up -d
~~~
# Start peers with local binary command
~~~shell script
$ cd fabric/cmd/peer
$ go build
~~~
~~~shell script
$ mv fabric/cmd/peer/peer ./peer0_org2
~~~
## copy core.yaml file and build peer env

~~~shell script
$ docker history hyperledger/fabric-peer:2.0.0
<missing>           9 months ago        /bin/sh -c #(nop)  ENV FABRIC_CFG_PATH=/etc/…   0B
$ docker exec -it peer0.thk_org1.thinking.com sh
# echo $FABRIC_CFG_PATH
# ls $FABRIC_CFG_PATH
# cat $FABRIC_CFG_PATH/core.yaml
$ docker cp peer0.thk_org1.thinking.com:/etc/hyperledger/fabric/core.yaml ./peer0_org2
$ docker cp peer0.thk_org1.thinking.com:/etc/hyperledger/fabric/core.yaml ./peer0_org2/core.yaml.comp
~~~
compare core.yaml with core.yaml.comp
~~~shell script
$ ln -s ../crypto-config/peerOrganizations/thk_org2.thinking.com/peers/peer0.thk_org2.thinking.com/tls ./peer0_org2/tls
$ ln -s ../crypto-config/peerOrganizations/thk_org2.thinking.com/peers/peer0.thk_org2.thinking.com/msp ./peer0_org2/msp
$ cd peer0_org2
$ source env_set_up.sh
$ ./peer node start
~~~
## fix errors
* panic: Could not create _lifecycle chaincodes install path: mkdir /var/hyperledger: permission denied
~~~shell script
$ sudo mkdir -p /var/hyperledger
$ sudo chown yong:yong -R /var/hyperledger
~~~
* Failed to create peer server (listen tcp 0.0.0.0:7051: bind: address already in use)

modify /etc/hosts, add:

127.0.0.1	peer0.thk_org2.thinking.com