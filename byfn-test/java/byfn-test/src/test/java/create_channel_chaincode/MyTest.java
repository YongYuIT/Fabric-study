package create_channel_chaincode;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;


import send_trans_with_ca_new_user.FabricUser;
import send_trans_with_ca_new_user.FabricUserEnrollment;

import static send_trans_with_ca_new_user.TransWithNewUser.*;

/*

直接借助byfn
$ bash byfn.sh up -i 1.4.6 -a
$ configtxgen -profile TwoOrgsChannel -channelID mytestchannel -outputCreateChannelTx channel-artifacts/mytestchannel.tx

*/


public class MyTest {

    private static final String FABRIC_CHANNEL_PATH = "/yong/codes/fabric-samples/first-network/channel-artifacts/";
    private static final String CHANNEL_NAME = "mytestchannel";

    @Test
    public void createChannel() throws Exception {
        //========================================================
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        FabricUser org1Admin = getOrg1AdminUser();
        hfclient.setUserContext(org1Admin);
        //========================================================
        Properties ordererProperties = new Properties();
        ordererProperties.setProperty("pemFile", ORDERER_TLS_PATH + "server.crt");
        ordererProperties.setProperty("hostnameOverride", "orderer.example.com");
        ordererProperties.setProperty("sslProvider", "openSSL");
        ordererProperties.setProperty("negotiationType", "TLS");
        Orderer orderer = hfclient.newOrderer("orderer.example.com", "grpcs://localhost:7050", ordererProperties);//此处一定要用grpcs
        //========================================================
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(FABRIC_CHANNEL_PATH + CHANNEL_NAME + ".tx"));
        //========================================================
        Channel newChannel = hfclient.newChannel(CHANNEL_NAME, orderer, channelConfiguration, hfclient.getChannelConfigurationSignature(channelConfiguration, org1Admin));

        Properties peer0_org1Properties = new Properties();
        peer0_org1Properties.setProperty("pemFile", ORG1_PEER0_TLS_PATH + "server.crt");
        peer0_org1Properties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peer0_org1Properties.setProperty("sslProvider", "openSSL");
        peer0_org1Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org1 = hfclient.newPeer("peer0_org1", "grpcs://localhost:7051", peer0_org1Properties);//此处一定要用grpcs
        newChannel.joinPeer(peer0_org1, createPeerOptions());

        //========================================================
        FabricUser org2Admin = getOrg2AdminUser();
        hfclient.setUserContext(org2Admin);
        //========================================================
        Properties peer0_org2Properties = new Properties();
        peer0_org2Properties.setProperty("pemFile", ORG2_PEER0_TLS_PATH + "server.crt");
        peer0_org2Properties.setProperty("hostnameOverride", "peer0.org2.example.com");
        peer0_org2Properties.setProperty("sslProvider", "openSSL");
        peer0_org2Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org2 = hfclient.newPeer("peer0_org2", "grpcs://localhost:9051", peer0_org2Properties);//此处一定要用grpcs
        newChannel.joinPeer(peer0_org2, createPeerOptions());

        newChannel.initialize();

        try {
            newChannel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return;
        }
        System.out.println("channel.initialize success");
    }

    public static FabricUser getOrg2AdminUser() throws Exception {
        File privateKeyFile = FabricUserEnrollment.getPrivateKeyFileFromPath(FABRIC_CONFIG_PATH + "peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/keystore/");
        File certificateFile = new File(FABRIC_CONFIG_PATH + "peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/signcerts/Admin@org2.example.com-cert.pem");
        FabricUserEnrollment org2AdminEnrollment = new FabricUserEnrollment(privateKeyFile, certificateFile);
        FabricUser org2Admin = new FabricUser();
        org2Admin.setName("FuckOrg2Admin");
        org2Admin.setMspId("Org2MSP");
        org2Admin.setEnrollment(org2AdminEnrollment);
        return org2Admin;
    }
}

/*

验证
$ docker exec -it cli /bin/bash
# peer channel list
2020-08-31 07:06:31.451 UTC [channelCmd] InitCmdFactory -> INFO 001 Endorser and orderer connections initialized
Channels peers has joined:
mytestchannel
mychannel

# export CORE_PEER_ADDRESS=peer0.org2.example.com:9051
# export CORE_PEER_LOCALMSPID=Org2MSP
# export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
# export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
# export CORE_PEER_TLS_CERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt
# export CORE_PEER_TLS_KEY_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.key
# peer channel list
2020-08-31 07:06:31.451 UTC [channelCmd] InitCmdFactory -> INFO 001 Endorser and orderer connections initialized
Channels peers has joined:
mytestchannel
mychannel

*/