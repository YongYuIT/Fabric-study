package org3_endorsement;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org3_endorsement.Config.*;

public class MyTestEndorsement {

    private PeerUser getUser() throws Exception {
        File privateKeyFile = PeerUserEnrollment.getPrivateKeyFileFromPath(ORG1_ADMIN_MSP_PATH + "keystore/");
        File certificateFile = PeerUserEnrollment.getCertificateFileFromPath(ORG1_ADMIN_MSP_PATH + "signcerts/");
        PeerUserEnrollment peerUserEnrollment = new PeerUserEnrollment(privateKeyFile, certificateFile);
        PeerUser user = new PeerUser();
        user.setName("peerOrg1Admin");
        user.setMspId("Org1MSP");
        user.setEnrollment(peerUserEnrollment);
        return user;
    }

    private HFClient getHFClient(User user) throws Exception {
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        hfclient.setUserContext(user);
        return hfclient;
    }

    private Channel getChannel(HFClient hfClient) throws Exception {


        Channel channel = hfClient.newChannel("mychannel");
        Peer peer0_org1 = hfClient.newPeer("peer0.org1.example.com", ORG1_PEER0_URL, TLSProperties.get(TLS_NAME.peer0_org1.name()));//此处一定要用grpcs
        channel.addPeer(peer0_org1);

        Peer peer0_org2 = hfClient.newPeer("peer0.org2.example.com", ORG2_PEER0_URL, TLSProperties.get(TLS_NAME.peer0_org2.name()));//此处一定要用grpcs
        channel.addPeer(peer0_org2);

        Orderer orderer = hfClient.newOrderer("orderer.example.com", ORDERER_URL, TLSProperties.get(TLS_NAME.orderer.name()));//此处一定要用grpcs
        channel.addOrderer(orderer);

        try {
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return null;
        }

        System.out.println("channel.initialize success");
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer0_org1);
        System.out.println("the current ledger blocks height:" + blockchainInfo.getHeight() + " ");

        return channel;
    }

    //测试背书策略 (org1 or org2) and org3
    //测试项目1：如果org1与org3参与背书，且背书结果一致，交易应该能提交
    //测试项目2：如果org1与org3参与背书，但是org3的状态数据库被恶意篡改，交易应该不能提交
    //测试项目3：继测试项目2，改为org2与org3参与背书，交易应该能提交
    //测试项目4：单独org1参与背书，交易不能提交
    //测试项目5：单独org3参与背书，交易不能提交

    /*
     * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
     * $ ./byfn.sh up -s couchdb
     * $ ./eyfn.sh up -s couchdb
     */

    @Test
    public void test0() throws Exception
    //测试连通性，两个组织条件下有效；三个组织条件下由于背书策略变更为'AND ('\''Org1MSP.peer'\'','\''Org2MSP.peer'\'','\''Org3MSP.peer'\'')'，故无效！
    {
        User user = getUser();
        HFClient hfClient = getHFClient(user);
        Channel channel = getChannel(hfClient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc").build();
        Collection<ProposalResponse> successful = new LinkedList<>();
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
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

    @Test
    public void test1() throws Exception {
        /*
         * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
         * $ docker exec -it Org3cli bash
         * 切换到org1
         * # export CORE_PEER_ADDRESS=peer0.org1.example.com:7051
         * # export CORE_PEER_LOCALMSPID="Org1MSP"
         * # export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
         * # export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
         * # peer chaincode install -n mycc_test_3orgs -v 1.0 -l golang -p github.com/chaincode/chaincode_example02/go/
         * 切换到org2
         * # export CORE_PEER_ADDRESS=peer0.org2.example.com:9051
         * # export CORE_PEER_LOCALMSPID="Org2MSP"
         * # export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
         * # export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
         * # peer chaincode install -n mycc_test_3orgs -v 1.0 -l golang -p github.com/chaincode/chaincode_example02/go/
         * 切换到org3
         * # export CORE_PEER_ADDRESS=peer0.org3.example.com:11051
         * # export CORE_PEER_LOCALMSPID="Org3MSP"
         * # export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org3.example.com/peers/peer0.org3.example.com/tls/ca.crt
         * # export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org3.example.com/users/Admin@org3.example.com/msp
         * # peer chaincode install -n mycc_test_3orgs -v 1.0 -l golang -p github.com/chaincode/chaincode_example02/go/
         * # peer chaincode instantiate -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc_test_3orgs -l golang -v 1.0 -c '{"Args":["init","a","100","b","200"]}' -P 'AND('\''Org3MSP.peer'\'',OR('\''Org1MSP.peer'\'','\''Org2MSP.peer'\''))'
         * # peer chaincode list --installed
         * # peer chaincode list --instantiated
         */
    }

}
