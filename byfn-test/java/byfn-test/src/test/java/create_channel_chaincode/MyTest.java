package create_channel_chaincode;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;
import send_trans_with_ca_new_user.FabricUser;
import send_trans_with_ca_new_user.FabricUserEnrollment;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;
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


    private static final String ccHome = "/yong/testcc/";
    private static final String ccName = "fuckcc20200831001";
    private static final String ccVersion = "fuckv1.0";
    private static final String ccPath = "chaincode_example02/go";
    private static final TransactionRequest.Type ccLang = TransactionRequest.Type.GO_LANG;

    @Test
    public void installCC() throws Exception {

        //========================================================
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(ccName).setVersion(ccVersion);
        chaincodeIDBuilder.setPath(ccPath);
        ChaincodeID chaincodeID = chaincodeIDBuilder.build();

        installOrg1(hfclient, chaincodeID);
        installOrg2(hfclient, chaincodeID);

    }

    @Test
    public void instantiateCC() throws Exception {

        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        FabricUser org1Admin = getOrg1AdminUser();
        hfclient.setUserContext(org1Admin);
        //========================================================
        Channel channel = configChannel(hfclient, CHANNEL_NAME);
        //========================================================

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(ccName).setVersion(ccVersion);
        chaincodeIDBuilder.setPath(ccPath);
        ChaincodeID chaincodeID = chaincodeIDBuilder.build();

        InstantiateProposalRequest instantiateProposalRequest = hfclient.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(300000);
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setChaincodeLanguage(ccLang);
        instantiateProposalRequest.setFcn("init");
        instantiateProposalRequest.setArgs("a", "500", "b", String.valueOf(200));
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("chaincodeendorsementpolicy.yaml"));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());
        Collection<ProposalResponse> proposals = new LinkedList<>();
        for (ProposalResponse pr : responses) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                proposals.add(pr);
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }

        try {
            String tid = channel.sendTransaction(proposals, org1Admin).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }
    }

    private static void installOrg1(HFClient hfclient, ChaincodeID chaincodeID) throws Exception {
        //========================================================
        FabricUser org1Admin = getOrg1AdminUser();
        hfclient.setUserContext(org1Admin);
        //========================================================
        InstallProposalRequest installProposalRequest = hfclient.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(new File(ccHome));
        installProposalRequest.setChaincodeLanguage(ccLang);
        //========================================================
        Collection<Peer> peersOrg1 = new ArrayList<>();
        Properties peer0_org1Properties = new Properties();
        peer0_org1Properties.setProperty("pemFile", ORG1_PEER0_TLS_PATH + "server.crt");
        peer0_org1Properties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peer0_org1Properties.setProperty("sslProvider", "openSSL");
        peer0_org1Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org1 = hfclient.newPeer("peer0_org1", "grpcs://localhost:7051", peer0_org1Properties);//此处一定要用grpcs
        peersOrg1.add(peer0_org1);
        Collection<ProposalResponse> responses = hfclient.sendInstallProposal(installProposalRequest, peersOrg1);
        for (ProposalResponse pr : responses) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }
    }

    private static void installOrg2(HFClient hfclient, ChaincodeID chaincodeID) throws Exception {
        //========================================================
        FabricUser org2Admin = getOrg2AdminUser();
        hfclient.setUserContext(org2Admin);
        //========================================================
        InstallProposalRequest installProposalRequest = hfclient.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(new File(ccHome));
        installProposalRequest.setChaincodeLanguage(ccLang);
        //========================================================
        Collection<Peer> peersOrg2 = new ArrayList<>();
        Properties peer0_org2Properties = new Properties();
        peer0_org2Properties.setProperty("pemFile", ORG2_PEER0_TLS_PATH + "server.crt");
        peer0_org2Properties.setProperty("hostnameOverride", "peer0.org2.example.com");
        peer0_org2Properties.setProperty("sslProvider", "openSSL");
        peer0_org2Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org2 = hfclient.newPeer("peer0_org2", "grpcs://localhost:9051", peer0_org2Properties);//此处一定要用grpcs
        peersOrg2.add(peer0_org2);
        Collection<ProposalResponse> responses = hfclient.sendInstallProposal(installProposalRequest, peersOrg2);
        for (ProposalResponse pr : responses) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }
    }

    private static FabricUser getOrg2AdminUser() throws Exception {
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

# peer chaincode list --installed
# peer chaincode list -C mytestchannel --instantiated
Get instantiated chaincodes on channel mytestchannel:
Name: fuckcc20200831001, Version: fuckv1.0, Path: chaincode_example02/go, Escc: escc, Vscc: vscc

# export CORE_PEER_ADDRESS=peer0.org2.example.com:9051
# export CORE_PEER_LOCALMSPID=Org2MSP
# export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
# export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
# export CORE_PEER_TLS_CERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt
# export CORE_PEER_TLS_KEY_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.key
# peer chaincode list -C mytestchannel --instantiated
Get instantiated chaincodes on channel mytestchannel:
Name: fuckcc20200831001, Version: fuckv1.0, Path: chaincode_example02/go, Escc: escc, Vscc: vscc



*/