package connect_to_channel;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class ByfnTransactionTest {

    @Test
    public void test() throws Exception {
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        File privateKeyFile = new File("/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/10d163966ccda9f4ffdc6605bd8faef8689e3fba2c69b154da75448b16449e4f_sk");
        File certificateFile = new File("/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
        //--------------------------------------------------------
        ByfnEnrollment byfnEnrollment = new ByfnEnrollment(privateKeyFile, certificateFile);
        ByfnUser user = new ByfnUser();
        user.setName("peerOrg1Admin");
        user.setMspId("Org1MSP");
        user.setEnrollment(byfnEnrollment);
        hfclient.setUserContext(user);
        //========================================================
        Channel channel = hfclient.newChannel("mychannel");
        Peer peer0_org1 = hfclient.newPeer("peer0_org1", "grpc://localhost:7051");
        channel.addPeer(peer0_org1);
        //if need to send transaction, need peer0_org1 and peer0_org2 endorsed
        /*
        peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n mycc -l golang -v 1.0 -c '{"Args":["init","a","100","b","200"]}' -P 'AND ('\''Org1MSP.peer'\'','\''Org2MSP.peer'\'')'
         */
        Peer peer0_org2 = hfclient.newPeer("peer0_org2", "grpc://localhost:9051");
        channel.addPeer(peer0_org2);
        //if need to send transaction, must add orderer
        Orderer orderer = hfclient.newOrderer("orderer", "grpc://localhost:7050");
        channel.addOrderer(orderer);
        try {
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return;
        }
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

    }

}
