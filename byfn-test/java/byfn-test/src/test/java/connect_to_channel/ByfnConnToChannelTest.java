package connect_to_channel;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ByfnConnToChannelTest {

    @Test
    public void Test() throws Exception {
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        File privateKeyFile = new File("/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/368b321139e659dbe994a2fe6849afd6f1ca7500936ca4139c4d4dee46dd27f1_sk");
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
        Peer peer = hfclient.newPeer("peer0", "grpc://localhost:7051");
        //since user 'peerOrg1Admin' register on peer0 instead of peer0_org2, user 'peerOrg1Admin' cannot query data from peer0_org2
        //peer = hfclient.newPeer("peer0_org2", "grpc://localhost:9051");
        channel.addPeer(peer);
        try {
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return;
        }
        //--------------------------------------------------------
        System.out.println("channel.initialize success");
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer);
        System.out.println("the current ledger blocks height:" + blockchainInfo.getHeight() + " ");
        //--------------------------------------------------------
        Set<String> peerChannels = hfclient.queryChannels(peer);
        peerChannels.forEach(System.out::println);
        //--------------------------------------------------------
        List<Query.ChaincodeInfo> installccs = hfclient.queryInstalledChaincodes(peer);
        installccs.forEach(cc -> System.out.println(cc.getPath() + "-->" + cc.getName()));
        List<Query.ChaincodeInfo> instantiatedChaincodes = channel.queryInstantiatedChaincodes(peer);
        instantiatedChaincodes.forEach(cc -> System.out.println(cc.getPath() + "-->" + cc.getName()));
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc").build();
        QueryByChaincodeRequest queryByChaincodeRequest = hfclient.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(new String[]{"a"});
        queryByChaincodeRequest.setFcn("query");
        queryByChaincodeRequest.setChaincodeID(ccId);
        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse pr : queryProposals) {
            if (!pr.isVerified() || pr.getStatus() != ChaincodeResponse.Status.SUCCESS) {
                System.out.println("Failed query proposal from peer!");
            } else {
                String payload = pr.getProposalResponse().getResponse().getPayload().toStringUtf8();
                System.out.println("Query a success : " + payload);
            }
        }
    }

}
