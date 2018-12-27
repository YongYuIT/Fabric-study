import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ByfnConnToChannel {

    @Test
    public void Test() throws Exception {
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        String name = "peerOrg1Admin";
        String org = "peerOrg1";
        File privateKeyFile = new File("/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/10d163966ccda9f4ffdc6605bd8faef8689e3fba2c69b154da75448b16449e4f_sk");
        File certificateFile = new File("/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
        //--------------------------------------------------------
        ByfnEnrollment byfnEnrollment = new ByfnEnrollment(privateKeyFile, certificateFile);
        ByfnUser user = new ByfnUser();
        user.setName(name);
        user.setMspId("Org1MSP");
        user.setEnrollment(byfnEnrollment);
        hfclient.setUserContext(user);
        //========================================================
        Channel channel = hfclient.newChannel("mychannel");
        Orderer orderer = hfclient.newOrderer("orderer", "grpc://localhost:7050");
        channel.addOrderer(orderer);
        Peer peer = hfclient.newPeer("peer0", "grpc://localhost:7051");
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
