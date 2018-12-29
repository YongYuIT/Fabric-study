package connect_to_channel;

import com.alibaba.fastjson.JSONObject;
import org.hyperledger.fabric.protos.common.Ledger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.io.File;

public class ByfnQueryHistoryTest {

    @Test
    public void Test() throws Exception {
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
        Peer peer = hfclient.newPeer("peer0", "grpc://localhost:7051");
        channel.addPeer(peer);
        try {
            channel.initialize();
        } catch (Throwable e) {
            System.out.println("channel.initialize error");
            e.printStackTrace();
            return;
        }
        //========================================================
        TransactionInfo transactionInfo = channel.queryTransactionByID("ddc70389da7e0e5d7c20218ea6c2345a4028f87def1f75d3ca5cdaa37f37b4e2");
        System.out.println("getValidationCode-->" + transactionInfo.getValidationCode().getNumber());

        BlockInfo blockInfo = channel.queryBlockByTransactionID("ddc70389da7e0e5d7c20218ea6c2345a4028f87def1f75d3ca5cdaa37f37b4e2");
        System.out.println("getChannelId-->" + blockInfo.getChannelId());
        System.out.println("getBlockNumber-->" + blockInfo.getBlockNumber());
        System.out.println("getDataHash-->" + JSONObject.toJSONString(blockInfo.getDataHash()));
        System.out.println("getPreviousHash-->" + JSONObject.toJSONString(blockInfo.getPreviousHash()));
        System.out.println("getTransactionCount-->" + blockInfo.getTransactionCount());
    }

}
