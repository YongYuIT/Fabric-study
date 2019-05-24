package org3_endorsement;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MyTestEndorsement {

    //测试背书策略 (org1 or org2) and org3
    //测试项目1：如果org1与org3参与背书，且背书结果一致，交易应该能提交
    //测试项目2：如果org1与org3参与背书，但是org3的状态数据库被恶意篡改，交易应该不能提交
    //测试项目3：继测试项目2，改为org2与org3参与背书，交易应该能提交
    //测试项目4：单独org1参与背书，交易不能提交
    //测试项目5：单独org3参与背书，交易不能提交

    private static final String FABRIC_CONFIG_PATH = "/mnt/hgfs/fabric-env/fabric-samples/first-network/crypto-config/";

    private static final String ORG1_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/";
    private static final String ORG1_ADMIN_MSP_PATH = ORG1_CONFIG_PATH + "users/Admin@org1.example.com/msp/";
    private static final String ORG1_PEER0_TLS_PATH = ORG1_CONFIG_PATH + "peers/peer0.org1.example.com/tls/";

    private static final String ORG2_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org2.example.com/";
    private static final String ORG2_PEER0_TLS_PATH = ORG2_CONFIG_PATH + "peers/peer0.org2.example.com/tls/";

    private static final String ORDERER_CONFIG_PATH = FABRIC_CONFIG_PATH + "ordererOrganizations/example.com/";
    private static final String ORDERER_TLS_PATH = ORDERER_CONFIG_PATH + "orderers/orderer.example.com/tls/";

    /*
     * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
     * $ ./byfn.sh up -s couchdb
     * $ ./eyfn.sh up -s couchdb
     * */

    @Test
    public void test0() throws Exception
    //测试连通性，两个组织条件下有效；三个组织条件下由于背书策略变更为'AND ('\''Org1MSP.peer'\'','\''Org2MSP.peer'\'','\''Org3MSP.peer'\'')'，故无效！
    {
        //========================================================
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        File privateKeyFile = event.PeerUserEnrollment.getPrivateKeyFileFromPath(ORG1_ADMIN_MSP_PATH + "keystore/");
        File certificateFile = event.PeerUserEnrollment.getCertificateFileFromPath(ORG1_ADMIN_MSP_PATH + "signcerts/");
        PeerUserEnrollment peerUserEnrollment = new PeerUserEnrollment(privateKeyFile, certificateFile);
        PeerUser user = new PeerUser();
        user.setName("peerOrg1Admin");
        user.setMspId("Org1MSP");
        user.setEnrollment(peerUserEnrollment);
        hfclient.setUserContext(user);
        //========================================================
        Channel channel = hfclient.newChannel("mychannel");
        //TLS info
        Properties peer0_org1Properties = new Properties();
        peer0_org1Properties.setProperty("pemFile", ORG1_PEER0_TLS_PATH + "server.crt");
        peer0_org1Properties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peer0_org1Properties.setProperty("sslProvider", "openSSL");
        peer0_org1Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org1 = hfclient.newPeer("peer0.org1.example.com", "grpcs://localhost:7051", peer0_org1Properties);//此处一定要用grpcs
        channel.addPeer(peer0_org1);

        //TLS info
        Properties peer0_org2Properties = new Properties();
        peer0_org2Properties.setProperty("pemFile", ORG2_PEER0_TLS_PATH + "server.crt");
        peer0_org2Properties.setProperty("hostnameOverride", "peer0.org2.example.com");
        peer0_org2Properties.setProperty("sslProvider", "openSSL");
        peer0_org2Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org2 = hfclient.newPeer("peer0.org2.example.com", "grpcs://localhost:9051", peer0_org2Properties);//此处一定要用grpcs
        channel.addPeer(peer0_org2);

        //TLS info
        Properties ordererProperties = new Properties();
        ordererProperties.setProperty("pemFile", ORDERER_TLS_PATH + "server.crt");
        ordererProperties.setProperty("hostnameOverride", "orderer.example.com");
        ordererProperties.setProperty("sslProvider", "openSSL");
        ordererProperties.setProperty("negotiationType", "TLS");
        Orderer orderer = hfclient.newOrderer("orderer.example.com", "grpcs://localhost:7050", ordererProperties);//此处一定要用grpcs
        channel.addOrderer(orderer);
        try {
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return;
        }
        System.out.println("channel.initialize success");
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer0_org1);
        System.out.println("the current ledger blocks height:" + blockchainInfo.getHeight() + " ");
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc").build();
        Collection<ProposalResponse> successful = new LinkedList<>();
        TransactionProposalRequest transactionProposalRequest = hfclient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[]{"a", "b", "1"});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(user);
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        for (ProposalResponse pr : invokePropResp) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                successful.add(pr);
            } else {
                System.out.println("failed");
            }
        }
        String tid = channel.sendTransaction(successful, user).get(10, TimeUnit.SECONDS).getTransactionID();
        System.out.println("tid-->" + tid);
        /*
         * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
         * $ docker exec -it cli bash
         * # peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
         */
    }


}
