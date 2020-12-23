package basic;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String FABRIC_CONFIG_PATH = "/yong/codes/fabric-samples/first-network/crypto-config/";

    public static final String ORG1_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/";
    public static final String ORG1_PEER0_TLS_PATH = ORG1_CONFIG_PATH + "peers/peer0.org1.example.com/tls/";

    public static final String ORG2_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org2.example.com/";
    public static final String ORG2_PEER0_TLS_PATH = ORG2_CONFIG_PATH + "peers/peer0.org2.example.com/tls/";

    public static final String ORDERER_CONFIG_PATH = FABRIC_CONFIG_PATH + "ordererOrganizations/example.com/";
    public static final String ORDERER_TLS_PATH = ORDERER_CONFIG_PATH + "orderers/orderer.example.com/tls/";


    public static void main(String[] args) throws Exception {
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        FabricUser org1Admin = getOrg1AdminUser();
        hfclient.setUserContext(org1Admin);
        //========================================================
        Channel channel = configChannel(hfclient);
        //========================================================
        sendTrans(hfclient, channel, org1Admin);
        //========================================================
        //queryTrans(channel);
    }

    public static FabricUser getOrg1AdminUser() throws Exception {
        File privateKeyFile = null;
        privateKeyFile = FabricUserEnrollment.getPrivateKeyFileFromPath(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/");
        File certificateFile = new File(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
        FabricUserEnrollment org1AdminEnrollment = new FabricUserEnrollment(privateKeyFile, certificateFile);
        FabricUser org1Admin = new FabricUser();
        org1Admin.setName("FuckOrg1Admin");
        org1Admin.setMspId("Org1MSP");
        org1Admin.setEnrollment(org1AdminEnrollment);
        return org1Admin;
    }

    public static Channel configChannel(HFClient hfclient) throws Exception {
        Channel channel = hfclient.newChannel("mychannel");

        Properties peer0_org1Properties = new Properties();
        peer0_org1Properties.setProperty("pemFile", ORG1_PEER0_TLS_PATH + "server.crt");
        peer0_org1Properties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peer0_org1Properties.setProperty("sslProvider", "openSSL");
        peer0_org1Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org1 = hfclient.newPeer("peer0_org1", "grpcs://localhost:7051", peer0_org1Properties);//此处一定要用grpcs
        channel.addPeer(peer0_org1);

        Properties peer0_org2Properties = new Properties();
        peer0_org2Properties.setProperty("pemFile", ORG2_PEER0_TLS_PATH + "server.crt");
        peer0_org2Properties.setProperty("hostnameOverride", "peer0.org2.example.com");
        peer0_org2Properties.setProperty("sslProvider", "openSSL");
        peer0_org2Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org2 = hfclient.newPeer("peer0_org2", "grpcs://localhost:9051", peer0_org2Properties);//此处一定要用grpcs
        channel.addPeer(peer0_org2);

        Properties ordererProperties = new Properties();
        ordererProperties.setProperty("pemFile", ORDERER_TLS_PATH + "server.crt");
        ordererProperties.setProperty("hostnameOverride", "orderer.example.com");
        ordererProperties.setProperty("sslProvider", "openSSL");
        ordererProperties.setProperty("negotiationType", "TLS");
        Orderer orderer = hfclient.newOrderer("orderer.example.com", "grpcs://localhost:7050", ordererProperties);//此处一定要用grpcs
        channel.addOrderer(orderer);

        try {
            System.out.println("yuyong-->0-->call start");
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error!!!!!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace();
            return null;
        }

        System.out.println("channel.initialize success!!!!!!!!!!!!!!!!!!!!!!");
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer0_org1);
        System.out.println("the current ledger blocks height:" + blockchainInfo.getHeight() + " ");

        return channel;
    }


    public static void queryTrans(Channel channel) throws Exception {
        String tid = "0df02b2ff44fd58b3d74db1338f21c7db71a28be91531ed09c172bc13aa911a5";

        TransactionInfo transactionInfo = channel.queryTransactionByID(tid);
        System.out.println("query tx-->" + transactionInfo.getTransactionID());
        System.out.println("query tx-->" + transactionInfo.getValidationCode());
        System.out.println("query tx-->" + transactionInfo.getEnvelope().getSignature());

        BlockInfo blockInfo = channel.queryBlockByTransactionID(tid);
        System.out.println("query block-->" + blockInfo.getBlockNumber());
        System.out.println("query block-->" + blockInfo.getDataHash());
        System.out.println("query block-->" + blockInfo.getPreviousHash());
        System.out.println("query block-->" + blockInfo.getTransactionCount());
    }

    public static void sendTrans(HFClient hfclient, Channel channel, FabricUser org1Admin) throws Exception {
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc").build();
        TransactionProposalRequest transactionProposalRequest = hfclient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[]{"b", "a", "1"});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(org1Admin);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        Collection<ProposalResponse> proposals = new LinkedList<>();
        for (ProposalResponse pr : invokePropResp) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                proposals.add(pr);
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }

        for (ProposalResponse pr : proposals) {
            System.out.printf("proposal use for invoke Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
        }
        //========================================================
        String tid = null;
        try {
            tid = channel.sendTransaction(proposals, org1Admin).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }
    }

}
