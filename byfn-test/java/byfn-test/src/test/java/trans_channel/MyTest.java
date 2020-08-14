package trans_channel;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;
import send_trans_with_new_user.FabricUser;
import send_trans_with_new_user.FabricUserEnrollment;

import java.io.File;
import java.util.Properties;

public class MyTest {

    private static final String FABRIC_CONFIG_PATH = "/yong/codes/fabric-samples/first-network/crypto-config/";

    public static final String ORG1_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/";
    public static final String ORG1_PEER0_TLS_PATH = ORG1_CONFIG_PATH + "peers/peer0.org1.example.com/tls/";

    public static final String ORG2_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org2.example.com/";
    public static final String ORG2_PEER0_TLS_PATH = ORG2_CONFIG_PATH + "peers/peer0.org2.example.com/tls/";

    public static final String ORDERER_CONFIG_PATH = FABRIC_CONFIG_PATH + "ordererOrganizations/example.com/";
    public static final String ORDERER_TLS_PATH = ORDERER_CONFIG_PATH + "orderers/orderer.example.com/tls/";

    @Test
    public void test() throws Exception {
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        FabricUser org1Admin = getOrg1AdminUser();
        hfclient.setUserContext(org1Admin);
        //========================================================
        Channel channel = configChannel(hfclient);
        //========================================================
        //queryTrans(SerializeUtils.serialize(channel));
        queryTrans(channel);
    }

    public void queryTrans(String channelInfo) throws Exception {
        Channel channel = (Channel) SerializeUtils.serializeToObject(channelInfo);
        String tid = "0338c021aa42d32bb02ff660a81ab0eb9a6551c22da8749abcde44490232d932";

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

    public void queryTrans(Channel channel) throws Exception {
        String tid = "0338c021aa42d32bb02ff660a81ab0eb9a6551c22da8749abcde44490232d932";

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

    public static FabricUser getOrg1AdminUser() throws Exception {
        //File privateKeyFile = FabricUserEnrollment.getPrivateKeyFileFromPath(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/");
        File certificateFile = new File(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
        FabricUserEnrollment org1AdminEnrollment = new FabricUserEnrollment(null, certificateFile);
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
}
