package conn_and_query_test;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import java.util.Collection;
import java.util.Properties;


public class ConnAndQueryTest {

    private static final String CRYPTO_HOME = "/home/yong/Desktop/env_init/fabric-samples/first-network/crypto-config/";
    private static final String ORG1_CRYPTO_HOME = "peerOrganizations/org1.example.com/";
    private static final String ORDERER_CRYPTO_HOME = "ordererOrganizations/example.com/";

    public static final String ORG1_PEER0_URL = "grpcs://localhost:7051";
    public static final String ORDERER_URL = "grpcs://localhost:7050";

    @Test
    public void test() throws Exception {
        //创建客户端对象并添加加密套件
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //-------------------------------------------------------------
        //创建并设置用户
        FabricUser user = new FabricUser(
                CRYPTO_HOME + ORG1_CRYPTO_HOME + "users/Admin@org1.example.com/msp/keystore/priv_sk",
                CRYPTO_HOME + ORG1_CRYPTO_HOME + "users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem"
        );
        user.setName("peerOrg1Admin");
        user.setMspId("Org1MSP");
        //设置hfclient对象当前用户，所有从hfclient对象发出的命令都是以这个用户身份执行的
        hfclient.setUserContext(user);
        //-------------------------------------------------------------
        //新建peer，orderer节点的连接信息
        Channel channel = hfclient.newChannel("mychannel");

        //orderer节点的连接信息
        Properties ordererProperties = new Properties();
        //crt：服务器的公钥，发给客户端，客户端用这个与服务器进行TLS通信
        ordererProperties.setProperty("pemFile", CRYPTO_HOME + ORDERER_CRYPTO_HOME + "orderers/orderer.example.com/tls/server.crt");
        ordererProperties.setProperty("hostnameOverride", "orderer.example.com");
        ordererProperties.setProperty("sslProvider", "openSSL");
        ordererProperties.setProperty("negotiationType", "TLS");
        Orderer orderer = hfclient.newOrderer("orderer.example.com", ORDERER_URL, ordererProperties);//此处一定要用grpcs
        channel.addOrderer(orderer);

        //peer0.org1节点的连接信息
        Properties peer0_org1Properties = new Properties();
        //crt：服务器的公钥，发给客户端，客户端用这个与服务器进行TLS通信
        peer0_org1Properties.setProperty("pemFile", CRYPTO_HOME + ORG1_CRYPTO_HOME + "" + "peers/peer0.org1.example.com/tls/server.crt");
        peer0_org1Properties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peer0_org1Properties.setProperty("sslProvider", "openSSL");
        peer0_org1Properties.setProperty("negotiationType", "TLS");
        Peer peer0_org1 = hfclient.newPeer("peer0_org1", ORG1_PEER0_URL, peer0_org1Properties);//此处一定要用grpcs
        channel.addPeer(peer0_org1);

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

        //-------------------------------------------------------------
        //执行查询（单节点，无需背书）

        //新写法，参考
        //https://github.com/hyperledger/fabric-sdk-java/blob/master/src/test/java/org/hyperledger/fabric/sdkintegration/End2endLifecycleIT.java
        QueryByChaincodeRequest queryByChaincodeRequest = hfclient.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs("a");
        queryByChaincodeRequest.setFcn("query");
        queryByChaincodeRequest.setChaincodeName("mycc");
        queryByChaincodeRequest.setChaincodeVersion("1");

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());
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
