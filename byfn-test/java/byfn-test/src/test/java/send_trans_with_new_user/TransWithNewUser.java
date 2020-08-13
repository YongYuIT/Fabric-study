package send_trans_with_new_user;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.junit.Test;

import java.io.*;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class TransWithNewUser {

    private static final String FABRIC_CONFIG_PATH = "/yong/codes/fabric-samples/first-network/crypto-config/";

    public static final String ORG1_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/";
    public static final String ORG1_PEER0_TLS_PATH = ORG1_CONFIG_PATH + "peers/peer0.org1.example.com/tls/";

    public static final String ORG2_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org2.example.com/";
    public static final String ORG2_PEER0_TLS_PATH = ORG2_CONFIG_PATH + "peers/peer0.org2.example.com/tls/";

    public static final String ORDERER_CONFIG_PATH = FABRIC_CONFIG_PATH + "ordererOrganizations/example.com/";
    public static final String ORDERER_TLS_PATH = ORDERER_CONFIG_PATH + "orderers/orderer.example.com/tls/";

    //用org1的admin用户发起交易，成功！
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
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc").build();
        TransactionProposalRequest transactionProposalRequest = hfclient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[]{"b", "a", "1"});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(org1Admin);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
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
        //========================================================
        try {
            String tid = channel.sendTransaction(proposals, org1Admin).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }

        //$ docker exec -it cli /bin/bash
        //# peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
    }


    //用fabric-ca创建org1的普通用户，交易成功！
    private static String newUserName = "User122@org1.example.com";

    @Test
    public void test1() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("sslProvider", "openSSL");//this conf allowed delete
        properties.setProperty("negotiationType", "TLS");//this conf allowed delete
        //properties.setProperty("pemFile", ORG1_CONFIG_PATH + "ca/ca.org1.example.com-cert.pem");//this way is ok
        properties.put("pemBytes", readFile(ORG1_CONFIG_PATH + "ca/ca.org1.example.com-cert.pem").getBytes());//this way is ok
        properties.setProperty("allowAllHostNames", "true");
        HFCAClient hfcaClient = HFCAClient.createNewInstance("https://0.0.0.0:7054", properties);
        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        //========================================================
        //登录fabric-ca服务器(ca_peerOrg1)
        FabricUser rootUser = new FabricUser();
        //用用户名密码登录，用户名密码在docker-compose-ca.yaml文件中
        Enrollment enrollment = hfcaClient.enroll("admin", "adminpw");
        rootUser.setEnrollment(enrollment);
        HFCAAffiliation hfcaAffiliation = hfcaClient.getHFCAAffiliations(rootUser);
        printAffiliation(hfcaAffiliation);
        //========================================================
        if (!checkHasAffiliation("com", hfcaAffiliation)) {
            hfcaClient.newHFCAAffiliation("com").create(rootUser);
            hfcaClient.newHFCAAffiliation("com.example").create(rootUser);
            hfcaClient.newHFCAAffiliation("com.example.org1").create(rootUser);
        }
        //========================================================
        RegistrationRequest registrationRequest = new RegistrationRequest(newUserName, "com.example.org1");
        registrationRequest.setType(HFCAClient.HFCA_TYPE_CLIENT);
        registrationRequest.getAttributes().add(new Attribute(HFCAClient.HFCA_ATTRIBUTE_HFREGISTRARROLES, HFCAClient.HFCA_TYPE_CLIENT));
        registrationRequest.getAttributes().add(new Attribute(HFCAClient.HFCA_ATTRIBUTE_HFREGISTRARDELEGATEROLES, HFCAClient.HFCA_TYPE_CLIENT));
        String resp = hfcaClient.register(registrationRequest, rootUser);
        System.out.println("resp-->" + resp);
        //========================================================
        //获取创建的证书
        Enrollment newUserEnrollment = hfcaClient.enroll(newUserName, resp);
        newUserEnrollment.getKey();
        String cert = newUserEnrollment.getCert();
        String privateKey = getPEMStringFromPrivateKey(newUserEnrollment.getKey());
        System.out.println("Cert-->" + cert);
        System.out.println("Pk-->" + privateKey);
        saveFile(cert, newUserName + "-cert.pem");
        saveFile(privateKey, newUserName + "_pk");
        //========================================================
        FabricUser newUser = new FabricUser();
        newUser.setName("FuckNewUser");
        newUser.setMspId("Org1MSP");
        newUser.setEnrollment(enrollment);
        //========================================================
        HFClient hfclient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient.setCryptoSuite(cryptoSuite);
        //========================================================
        hfclient.setUserContext(newUser);
        //========================================================
        Channel channel = configChannel(hfclient);
        //========================================================
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("mycc").build();
        TransactionProposalRequest transactionProposalRequest = hfclient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[]{"b", "a", "1"});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(newUser);
        //========================================================
        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
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
        //========================================================
        try {
            String tid = channel.sendTransaction(proposals, newUser).get(10, TimeUnit.SECONDS).getTransactionID();
            System.out.println("transact success tid-->" + tid);
        } catch (Exception e) {
            System.out.println("transact failed-->" + e.getMessage());
        }
        //$ docker exec -it cli /bin/bash
        //# peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
    }

    public static FabricUser getOrg1AdminUser() throws Exception {
        File privateKeyFile = FabricUserEnrollment.getPrivateKeyFileFromPath(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/");
        File certificateFile = new File(FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
        FabricUserEnrollment org1AdminEnrollment = new FabricUserEnrollment(privateKeyFile, certificateFile);
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


    private void printAffiliation(HFCAAffiliation hfcaAffiliation) throws Exception {
        System.out.println("-->" + hfcaAffiliation.getName());
        for (HFCAAffiliation child : hfcaAffiliation.getChildren()) {
            printAffiliation(child);
        }
    }

    private boolean checkHasAffiliation(String name, HFCAAffiliation hfcaAffiliation) throws Exception {
        if (hfcaAffiliation.getName().equalsIgnoreCase(name)) {
            return true;
        } else {
            for (HFCAAffiliation child : hfcaAffiliation.getChildren()) {
                if (checkHasAffiliation(name, child)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getPEMStringFromPrivateKey(PrivateKey privateKey) throws IOException {
        StringWriter pemStrWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(pemStrWriter);
        pemWriter.writeObject(privateKey);
        pemWriter.close();
        return pemStrWriter.toString();
    }

    public static void saveFile(String content, String fileName) throws Exception {
        FileWriter fwriter = new FileWriter(fileName);
        fwriter.write(content);
        fwriter.flush();
        fwriter.close();
    }

    public static String readFile(String filePath) throws Exception {
        StringBuilder result = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String s = "";
        while ((s = br.readLine()) != null) {
            result.append(System.lineSeparator() + s);
        }
        br.close();
        return result.toString();
    }

}
