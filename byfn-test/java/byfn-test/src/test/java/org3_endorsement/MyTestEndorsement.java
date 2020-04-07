package org3_endorsement;

import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
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
        Peer peer0_org1 = hfClient.newPeer("peer0_org1", ORG1_PEER0_URL, TLSProperties.get(TLS_NAME.peer0_org1.name()));//此处一定要用grpcs
        channel.addPeer(peer0_org1);

        Peer peer0_org2 = hfClient.newPeer("peer0_org2", ORG2_PEER0_URL, TLSProperties.get(TLS_NAME.peer0_org2.name()));//此处一定要用grpcs
        channel.addPeer(peer0_org2);

        Peer peer0_org3 = hfClient.newPeer("peer0_org3", ORG3_PEER0_URL, TLSProperties.get(TLS_NAME.peer0_org3.name()));//此处一定要用grpcs
        channel.addPeer(peer0_org3);


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

    private TransactionProposalRequest getProposalReq(HFClient hfClient, ChaincodeID ccId, User user) {
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[]{"b", "a", "1"});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(user);
        return transactionProposalRequest;
    }

    private TransactionProposalRequest getProposalReqTest7(HFClient hfClient, ChaincodeID ccId, User user) {
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setFcn("add_age");
        transactionProposalRequest.setArgs(new String[]{"10000001", "3"});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(user);
        return transactionProposalRequest;
    }


    private Collection<ProposalResponse> getProposal(Collection<ProposalResponse> invokePropResp) throws Exception {
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

        return proposals;
    }

    private Collection<ProposalResponse> getProposalTest1(Collection<ProposalResponse> invokePropResp) {

        Collection<ProposalResponse> proposals = new LinkedList<>();
        for (ProposalResponse pr : invokePropResp) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                if (!pr.getPeer().getName().equalsIgnoreCase("peer0_org2")) {
                    proposals.add(pr);
                }
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }

        for (ProposalResponse pr : proposals) {
            System.out.printf("proposal use for invoke Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
        }

        return proposals;
    }

    private Collection<ProposalResponse> getProposalTest2(Collection<ProposalResponse> invokePropResp) {

        Collection<ProposalResponse> proposals = new LinkedList<>();
        for (ProposalResponse pr : invokePropResp) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                if (!pr.getPeer().getName().equalsIgnoreCase("peer0_org1")) {
                    proposals.add(pr);
                }
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }

        for (ProposalResponse pr : proposals) {
            System.out.printf("proposal use for invoke Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
        }

        return proposals;
    }

    private Collection<ProposalResponse> getProposalTest3(Collection<ProposalResponse> invokePropResp) {

        Collection<ProposalResponse> proposals = new LinkedList<>();
        for (ProposalResponse pr : invokePropResp) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                if (!pr.getPeer().getName().equalsIgnoreCase("peer0_org3")) {
                    proposals.add(pr);
                }
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }

        for (ProposalResponse pr : proposals) {
            System.out.printf("proposal use for invoke Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
        }

        return proposals;
    }

    private Collection<ProposalResponse> getProposalTest7(Collection<ProposalResponse> invokePropResp) throws Exception {
        Collection<ProposalResponse> proposals = new LinkedList<>();
        for (ProposalResponse pr : invokePropResp) {
            if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                printRWSet(pr);
                //if (!pr.getPeer().getName().equalsIgnoreCase("peer0_org1"))
                proposals.add(pr);
            } else {
                System.out.printf("failed transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
            }
        }

        for (ProposalResponse pr : proposals) {
            System.out.printf("proposal use for invoke Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
        }

        return proposals;
    }

    private void printRWSet(ProposalResponse pr) throws Exception {
        TxReadWriteSetInfo info = pr.getChaincodeActionResponseReadWriteSetInfo();
        for (int i = 0; i < info.getNsRwsetCount(); i++) {
            TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo = info.getNsRwsetInfo(i);
            System.out.println("===========================write set");
            for (KvRwset.KVWrite kvWrite : nsRwsetInfo.getRwset().getWritesList()) {
                System.out.println(kvWrite.getKey() + "-->" + kvWrite.getValue().toStringUtf8());
            }
            System.out.println("===========================read set");
            for (KvRwset.KVRead kvRead : nsRwsetInfo.getRwset().getReadsList()) {
                System.out.println(kvRead.getKey());
            }
            System.out.println("===========================over");
        }
    }

    //测试背书策略 (org1 or org2) and org3
    //测试项目1：如果org1与org3参与背书，且背书结果一致，交易应该能提交
    //测试项目2：如果org2与org3参与背书，且背书结果一致，交易应该能提交
    //测试项目3：如果org1与org2参与背书，且背书结果一致，交交易应该不能提交
    //测试项目4：如果org1与org3参与背书，但是org3的状态数据库被恶意篡改，交易应该不能提交
    //测试项目5：单独org1参与背书，交易不能提交
    //测试项目6：单独org3参与背书，交易不能提交
    //测试项目7：org1、org2、org3参与背书，其中org1与org2背书结果冲突（但是都可以成功背书），org2与org3背书结果一致，交易应该能提交
    //测试项目8：org1、org2、org3参与背书，其中org3与org1背书结果冲突（但是都可以成功背书），org2与org1背书结果一致，交易应该不能提交

    /*
     * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
     * $ ./byfn.sh up -s couchdb
     * $ ./eyfn.sh up -s couchdb
     */

    @Test
    public void test0() throws Exception {
        /* 预备测试，连通性测试
         * $ cd /mnt/hgfs/fabric-env/fabric-samples/first-network
         * $ docker exec -it cli bash
         * # peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
         */
        User user = getUser();
        HFClient hfClient = getHFClient(user);
        Channel channel = getChannel(hfClient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc").build();
        TransactionProposalRequest transactionProposalRequest = getProposalReq(hfClient, ccId, user);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        Collection<ProposalResponse> proposals = getProposal(invokePropResp);
        try {
            String tid = channel.sendTransaction(proposals, user).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }
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
         * # peer chaincode list -C mychannel --instantiated
         */

        User user = getUser();
        HFClient hfClient = getHFClient(user);
        Channel channel = getChannel(hfClient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc_test_3orgs").build();
        TransactionProposalRequest transactionProposalRequest = getProposalReq(hfClient, ccId, user);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        Collection<ProposalResponse> proposals = getProposalTest1(invokePropResp);
        try {
            String tid = channel.sendTransaction(proposals, user).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }

    }

    @Test
    public void test2() throws Exception {
        User user = getUser();
        HFClient hfClient = getHFClient(user);
        Channel channel = getChannel(hfClient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc_test_3orgs").build();
        TransactionProposalRequest transactionProposalRequest = getProposalReq(hfClient, ccId, user);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        Collection<ProposalResponse> proposals = getProposalTest2(invokePropResp);
        try {
            String tid = channel.sendTransaction(proposals, user).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }
    }

    @Test
    public void test3() throws Exception {
        User user = getUser();
        HFClient hfClient = getHFClient(user);
        Channel channel = getChannel(hfClient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc_test_3orgs").build();
        TransactionProposalRequest transactionProposalRequest = getProposalReq(hfClient, ccId, user);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        Collection<ProposalResponse> proposals = getProposalTest3(invokePropResp);
        try {
            String tid = channel.sendTransaction(proposals, user).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }
    }


    @Test
    public void test4() throws Exception {

        /*
         * $ docker ps | grep couchdb4
         * 访问 http://0.0.0.0:9984/_utils/
         * 如果不知道如何修改couchDB数据，直接将peer0.org3的couchDB数据删除
         */

        User user = getUser();
        HFClient hfClient = getHFClient(user);
        Channel channel = getChannel(hfClient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc_test_3orgs").build();
        TransactionProposalRequest transactionProposalRequest = getProposalReq(hfClient, ccId, user);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        Collection<ProposalResponse> proposals = getProposalTest1(invokePropResp);
        try {
            String tid = channel.sendTransaction(proposals, user).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }

    }


    @Test
    public void test7() throws Exception {

        /*
         * $ cd /mnt/hgfs/fabric-env/fabric-samples/chaincode
         * $ mkdir -p rich_query/go/
         * $ cd rich_query/go/
         * $ cp /mnt/hgfs/proj/Go-Stu/smart_contract/rich_query.go .
         * $ docker exec -it Org3cli bash
         * 切换到org1
         * # export CORE_PEER_ADDRESS=peer0.org1.example.com:7051
         * # export CORE_PEER_LOCALMSPID="Org1MSP"
         * # export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
         * # export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
         * # peer chaincode install -n rich_query1001 -v 1.0 -l golang -p github.com/chaincode/rich_query/go/
         * 切换到org2
         * # export CORE_PEER_ADDRESS=peer0.org2.example.com:9051
         * # export CORE_PEER_LOCALMSPID="Org2MSP"
         * # export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
         * # export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
         * # peer chaincode install -n rich_query1001 -v 1.0 -l golang -p github.com/chaincode/rich_query/go/
         * 切换到org3
         * # export CORE_PEER_ADDRESS=peer0.org3.example.com:11051
         * # export CORE_PEER_LOCALMSPID="Org3MSP"
         * # export CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org3.example.com/peers/peer0.org3.example.com/tls/ca.crt
         * # export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org3.example.com/users/Admin@org3.example.com/msp
         * # peer chaincode install -n rich_query1001 -v 1.0 -l golang -p github.com/chaincode/rich_query/go/
         * # peer chaincode instantiate -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n rich_query1001 -l golang -v 1.0 -c '{"Args":[]}' -P 'AND('\''Org3MSP.peer'\'',OR('\''Org1MSP.peer'\'','\''Org2MSP.peer'\''))'
         * # peer chaincode list --installed
         * # peer chaincode list -C mychannel --instantiated
         * # peer chaincode invoke -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n rich_query1001 --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses peer0.org2.example.com:9051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt --peerAddresses peer0.org3.example.com:11051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org3.example.com/peers/peer0.org3.example.com/tls/ca.crt -c '{"Args":["put_kv","10000001","{\"address\":\"ShenZhen city,GD,China\",\"age\":20,\"country\":\"CN\",\"gender\":\"M\",\"name\":\"yong\",\"phone_num\":\"110\",\"stu_no\":\"10000001\"}"]}'
         * # peer chaincode invoke -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n rich_query1001 --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses peer0.org2.example.com:9051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt --peerAddresses peer0.org3.example.com:11051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org3.example.com/peers/peer0.org3.example.com/tls/ca.crt -c '{"Args":["add_age","10000001","1"]}'
         * # peer chaincode query -C mychannel -n rich_query1001 -c '{"Args":["get_value","10000001"]}'
         * 访问 http://0.0.0.0:9984/_utils/ 直接手动改数据库（peer0.org3）
         * 访问 http://0.0.0.0:5984/_utils/ 直接手动改数据库（peer0.org1）
         */

        User user = getUser();
        HFClient hfClient = getHFClient(user);
        Channel channel = getChannel(hfClient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("rich_query1001").build();
        TransactionProposalRequest transactionProposalRequest = getProposalReqTest7(hfClient, ccId, user);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        Collection<ProposalResponse> proposals = getProposalTest7(invokePropResp);
        try {
            String tid = channel.sendTransaction(proposals, user).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
            //org3被篡改：transact failed-->java.lang.IllegalArgumentException: The proposal responses have 2 inconsistent groups with 0 that are invalid. Expected all to be consistent and none to be invalid.
            //org1被篡改：transact failed-->java.lang.IllegalArgumentException: The proposal responses have 2 inconsistent groups with 0 that are invalid. Expected all to be consistent and none to be invalid.
            //所以，只要提案的读写集不一致，都提交不上去。但是客户端程序可以在保证满足背书策略的前提下，剔除那些不一致提案，使交易正常进行
            //另外，在剔除peer0_org1（org1被篡改情况下）提案后，交易会正常进行。交易完成后，peer0_org1会写入最新的正确值，区块链账本又恢复了一致性！
        }
    }
}
