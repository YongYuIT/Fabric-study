package docker;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class BuildTools {
    public static void buildOrderer(String fabric_version, String ordererName) throws Exception {
        DockerConfig config = new DockerConfig();
        config.setVersion("'2'");
        config.setVolumes(new DockerConfig.volumeConfig());
        config.getVolumes().setREPLACED("DELETE");
        config.setServices(new DockerConfig.ServiceConfig());
        config.getServices().setREPLACED(new BaseService());
        config.getServices().getREPLACED().setCommand("orderer");
        config.getServices().getREPLACED().setContainer_name(ordererName);
        config.getServices().getREPLACED().setEnvironment(new ArrayList<String>());
        config.getServices().getREPLACED().getEnvironment().add("FABRIC_LOGGING_SPEC=INFO");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_LISTENADDRESS=0.0.0.0");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_GENESISMETHOD=file");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_GENESISFILE=/var/hyperledger/orderer/orderer.genesis.block");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_LOCALMSPID=OrdererMSP");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_LOCALMSPDIR=/var/hyperledger/orderer/msp");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_TLS_ENABLED=true");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_TLS_PRIVATEKEY=/var/hyperledger/orderer/tls/server.key");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_TLS_CERTIFICATE=/var/hyperledger/orderer/tls/server.crt");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_TLS_ROOTCAS=[/var/hyperledger/orderer/tls/ca.crt]");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_KAFKA_TOPIC_REPLICATIONFACTOR=1");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_KAFKA_VERBOSE=true");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE=/var/hyperledger/orderer/tls/server.crt");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY=/var/hyperledger/orderer/tls/server.key");
        config.getServices().getREPLACED().getEnvironment().add("ORDERER_GENERAL_CLUSTER_ROOTCAS=[/var/hyperledger/orderer/tls/ca.crt]");
        config.getServices().getREPLACED().setImage("hyperledger/fabric-orderer:" + fabric_version);
        config.getServices().getREPLACED().setPorts(new ArrayList<String>());
        config.getServices().getREPLACED().getPorts().add("7050:7050");
        config.getServices().getREPLACED().setWorking_dir("/opt/gopath/src/github.com/hyperledger/fabric");
        config.getServices().getREPLACED().setVolumes(new ArrayList<String>());
        config.getServices().getREPLACED().getVolumes().add("../channel-artifacts/genesis.block:/var/hyperledger/orderer/orderer.genesis.block");
        config.getServices().getREPLACED().getVolumes().add("../crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/msp:/var/hyperledger/orderer/msp");
        config.getServices().getREPLACED().getVolumes().add("../crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/:/var/hyperledger/orderer/tls");
        config.getServices().getREPLACED().getVolumes().add("orderer.example.com:/var/hyperledger/production/orderer");
        config.getServices().getREPLACED().setExtra_hosts(new ArrayList<String>());
        config.getServices().getREPLACED().getExtra_hosts().add("orderer.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer0.org1.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer0.org2.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer1.org2.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer0.org3.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer1.org3.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer2.org3.example.com:192.168.186.138");

        String yaml = getYml(config);
        yaml = yaml.replace("REPLACED", ordererName);
        yaml = yaml.replace("DELETE", "");
        yaml = yaml.replace("'''", "'");
        yaml = yaml.replace("!", "#");
        System.out.println(yaml);
        saveFile(yaml, "docker-compose-orderer.yaml");
    }

    public static void buildPeer(String fabric_version, String mspid, String peerName, int port, String gospurl) throws Exception {
        DockerConfig config = new DockerConfig();
        config.setVersion("'2'");
        config.setVolumes(new DockerConfig.volumeConfig());
        config.getVolumes().setREPLACED("DELETE");
        config.setServices(new DockerConfig.ServiceConfig());
        config.getServices().setREPLACED(new BaseService());
        config.getServices().getREPLACED().setCommand("peer node start");
        config.getServices().getREPLACED().setContainer_name(peerName);
        config.getServices().getREPLACED().setEnvironment(new ArrayList<String>());
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_ID=" + peerName);
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_ADDRESS=" + peerName + ":" + port);
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_LISTENADDRESS=0.0.0.0:" + port);
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_CHAINCODEADDRESS=" + peerName + ":" + (port + 1));
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_CHAINCODELISTENADDRESS=0.0.0.0" + ":" + (port + 1));
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_GOSSIP_BOOTSTRAP=" + gospurl);
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_GOSSIP_EXTERNALENDPOINT=" + peerName + ":" + port);
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_LOCALMSPID=" + mspid);
        config.getServices().getREPLACED().getEnvironment().add("FABRIC_LOGGING_SPEC=INFO");
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_TLS_ENABLED=true");
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_GOSSIP_USELEADERELECTION=true");
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_GOSSIP_ORGLEADER=false");
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_PROFILE_ENABLED=true");
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_TLS_CERT_FILE=/etc/hyperledger/fabric/tls/server.crt");
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_TLS_KEY_FILE=/etc/hyperledger/fabric/tls/server.key");
        config.getServices().getREPLACED().getEnvironment().add("CORE_PEER_TLS_ROOTCERT_FILE=/etc/hyperledger/fabric/tls/ca.crt");
        config.getServices().getREPLACED().setImage("hyperledger/fabric-peer:" + fabric_version);
        config.getServices().getREPLACED().setPorts(new ArrayList<String>());
        config.getServices().getREPLACED().getPorts().add(port + ":" + port);
        config.getServices().getREPLACED().setWorking_dir("/opt/gopath/src/github.com/hyperledger/fabric/peer");
        config.getServices().getREPLACED().setVolumes(new ArrayList<String>());
        config.getServices().getREPLACED().getVolumes().add("../crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/msp:/etc/hyperledger/fabric/msp");
        config.getServices().getREPLACED().getVolumes().add("../crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls:/etc/hyperledger/fabric/tls");
        config.getServices().getREPLACED().getVolumes().add(peerName + ":/var/hyperledger/production");
        config.getServices().getREPLACED().setExtra_hosts(new ArrayList<String>());
        config.getServices().getREPLACED().getExtra_hosts().add("orderer.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer0.org1.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer0.org2.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer1.org2.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer0.org3.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer1.org3.example.com:192.168.186.138");
        config.getServices().getREPLACED().getExtra_hosts().add("peer2.org3.example.com:192.168.186.138");


        String yaml = getYml(config);
        yaml = yaml.replace("REPLACED", peerName);
        yaml = yaml.replace("DELETE", "");
        yaml = yaml.replace("'''", "'");
        yaml = yaml.replace("!", "#");
        System.out.println(yaml);
        saveFile(yaml, "docker-compose-" + peerName + ".yaml");
    }

    private static String getYml(Object object) {
        StringWriter stringWriter = new StringWriter();
        YamlWriter writer = new YamlWriter(stringWriter);
        try {
            writer.write(object);
            writer.close();
        } catch (YamlException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static void saveFile(String content, String fileName) throws Exception {
        FileWriter fwriter = new FileWriter(fileName);
        fwriter.write(content);
        fwriter.flush();
        fwriter.close();
    }
}

