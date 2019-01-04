package query_based_couch;

import com.alibaba.fastjson.JSONObject;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class QueryTest {

    private static final String FABRIC_CONFIG_PATH = "/home/yong/fabric-samples/first-network/crypto-config/";

    @Test
    public void test() throws Exception {
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        File privateKeyFile = PeerUserEnrollment.getPrivateKeyFileFromPath(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/");
        File certificateFile = new File(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
        PeerUserEnrollment peerUserEnrollment = new PeerUserEnrollment(privateKeyFile, certificateFile);
        PeerUser user = new PeerUser();
        user.setName("peerOrg1Admin");
        user.setMspId("Org1MSP");
        user.setEnrollment(peerUserEnrollment);
        hfclient.setUserContext(user);
        //========================================================
        Channel channel = hfclient.newChannel("mychannel");
        Peer peer = hfclient.newPeer("peer0", "grpc://localhost:7051");
        channel.addPeer(peer);
        Orderer orderer = hfclient.newOrderer("orderer", "grpc://localhost:7050");
        channel.addOrderer(orderer);
        try {
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return;
        }
        System.out.println("channel.initialize success");
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer);
        System.out.println("the current ledger blocks height:" + blockchainInfo.getHeight() + " ");
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("rich_query").build();
        if (1 == 0) {
            QueryByChaincodeRequest queryByChaincodeRequest = hfclient.newQueryProposalRequest();
            queryByChaincodeRequest.setArgs(new String[]{"10000001"});
            queryByChaincodeRequest.setFcn("get_value");
            queryByChaincodeRequest.setChaincodeID(ccId);
            Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest);
            for (ProposalResponse pr : queryProposals) {
                if (!pr.isVerified() || pr.getStatus() != ChaincodeResponse.Status.SUCCESS) {
                    System.out.println("Failed query proposal from peer-->" + pr.getProposalResponse().getPayload().toStringUtf8());
                } else {
                    String payload = pr.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    System.out.println("Query a success : " + payload);
                }
            }
        }

        //========================================================
        if (1 == 1) {
            Student student = new Student();
            student.setAddress("ShenZhen city,GD,China");
            student.setAge(18);
            student.setCountry("CN");
            student.setGender("F");
            student.setName("ben");
            student.setPhone_num("110");
            student.setStu_no("10000003");

            System.out.println(JSONObject.toJSONString(student));

            TransactionProposalRequest transactionProposalRequest = hfclient.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(ccId);
            transactionProposalRequest.setFcn("put_kv");
            transactionProposalRequest.setArgs(new String[]{student.getStu_no(), JSONObject.toJSONString(student)});
            transactionProposalRequest.setProposalWaitTime(300000);
            transactionProposalRequest.setUserContext(user);

            Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
            Collection<ProposalResponse> successful = new LinkedList<>();
            for (ProposalResponse pr : invokePropResp) {
                if (pr.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                    System.out.printf("successful transaction proposal response Txid : %s from peer: %s\n", pr.getTransactionID(), pr.getPeer().getName());
                    successful.add(pr);
                } else {
                    System.out.println("failed-->" + pr.getProposalResponse().getPayload().toStringUtf8());
                }
            }
            String tid = channel.sendTransaction(successful, user).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("tid-->" + tid);
        }

        //========================================================



    }
}
