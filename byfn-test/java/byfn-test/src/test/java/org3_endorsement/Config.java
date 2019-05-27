package org3_endorsement;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {

    public static final String FABRIC_CONFIG_PATH = "/mnt/hgfs/fabric-env/fabric-samples/first-network/crypto-config/";
    public static final String FABRIC_CONFIG_PATH_3ORG = "/mnt/hgfs/fabric-env/fabric-samples/first-network/org3-artifacts/crypto-config/";

    public static final String ORG1_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org1.example.com/";
    public static final String ORG1_ADMIN_MSP_PATH = ORG1_CONFIG_PATH + "users/Admin@org1.example.com/msp/";
    public static final String ORG1_PEER0_TLS_PATH = ORG1_CONFIG_PATH + "peers/peer0.org1.example.com/tls/";
    public static final String ORG1_PEER0_URL = "grpcs://localhost:7051";

    public static final String ORG2_CONFIG_PATH = FABRIC_CONFIG_PATH + "peerOrganizations/org2.example.com/";
    public static final String ORG2_PEER0_TLS_PATH = ORG2_CONFIG_PATH + "peers/peer0.org2.example.com/tls/";
    public static final String ORG2_PEER0_URL = "grpcs://localhost:9051";

    public static final String ORG3_CONFIG_PATH = FABRIC_CONFIG_PATH_3ORG + "peerOrganizations/org3.example.com/";
    public static final String ORG3_ADMIN_MSP_PATH = ORG3_CONFIG_PATH + "users/Admin@org3.example.com/msp/";
    public static final String ORG3_PEER0_TLS_PATH = ORG3_CONFIG_PATH + "peers/peer0.org3.example.com/tls/";
    public static final String ORG3_PEER0_URL = "grpcs://localhost:11051";

    public static final String ORDERER_CONFIG_PATH = FABRIC_CONFIG_PATH + "ordererOrganizations/example.com/";
    public static final String ORDERER_TLS_PATH = ORDERER_CONFIG_PATH + "orderers/orderer.example.com/tls/";
    public static final String ORDERER_URL = "grpcs://localhost:7050";


    public static final Map<String, Properties> TLSProperties = new HashMap<>();

    public enum TLS_NAME {
        peer0_org1,
        peer0_org2,
        peer0_org3,
        orderer
    }

    static {
        Properties peer0_org1Properties = new Properties();
        peer0_org1Properties.setProperty("pemFile", ORG1_PEER0_TLS_PATH + "server.crt");
        peer0_org1Properties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peer0_org1Properties.setProperty("sslProvider", "openSSL");
        peer0_org1Properties.setProperty("negotiationType", "TLS");
        TLSProperties.put(TLS_NAME.peer0_org1.name(), peer0_org1Properties);

        Properties peer0_org2Properties = new Properties();
        peer0_org2Properties.setProperty("pemFile", ORG2_PEER0_TLS_PATH + "server.crt");
        peer0_org2Properties.setProperty("hostnameOverride", "peer0.org2.example.com");
        peer0_org2Properties.setProperty("sslProvider", "openSSL");
        peer0_org2Properties.setProperty("negotiationType", "TLS");
        TLSProperties.put(TLS_NAME.peer0_org2.name(), peer0_org2Properties);

        Properties peer0_org3Properties = new Properties();
        peer0_org3Properties.setProperty("pemFile", ORG3_PEER0_TLS_PATH + "server.crt");
        peer0_org3Properties.setProperty("hostnameOverride", "peer0.org3.example.com");
        peer0_org3Properties.setProperty("sslProvider", "openSSL");
        peer0_org3Properties.setProperty("negotiationType", "TLS");
        TLSProperties.put(TLS_NAME.peer0_org3.name(), peer0_org3Properties);

        Properties ordererProperties = new Properties();
        ordererProperties.setProperty("pemFile", ORDERER_TLS_PATH + "server.crt");
        ordererProperties.setProperty("hostnameOverride", "orderer.example.com");
        ordererProperties.setProperty("sslProvider", "openSSL");
        ordererProperties.setProperty("negotiationType", "TLS");
        TLSProperties.put(TLS_NAME.orderer.name(), ordererProperties);
    }
}
