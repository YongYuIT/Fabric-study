import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.StringWriter;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        buildPeerOrgs();
        System.out.println("-------------------------------------");
        buildOrdererOrgs();
    }


    private static void buildOrdererOrgs() {
        OrdererOrgConfigSet ordererOrgConfigSet = new OrdererOrgConfigSet();
        ordererOrgConfigSet.setOrdererOrgs(new ArrayList<Config>());
        Config config = new Config();
        config.setEnableNodeOUs(true);
        config.setName("Orderer");
        config.setDomain("example.com");
        config.setSpecs(new ArrayList<HostConfig>());
        HostConfig hostConfig = new HostConfig();
        hostConfig.setHostname("orderer");
        config.getSpecs().add(hostConfig);
        hostConfig = new HostConfig();
        hostConfig.setHostname("orderer2");
        config.getSpecs().add(hostConfig);
        hostConfig = new HostConfig();
        hostConfig.setHostname("orderer3");
        config.getSpecs().add(hostConfig);
        hostConfig = new HostConfig();
        hostConfig.setHostname("orderer4");
        config.getSpecs().add(hostConfig);
        hostConfig = new HostConfig();
        hostConfig.setHostname("orderer5");
        config.getSpecs().add(hostConfig);
        ordererOrgConfigSet.getOrdererOrgs().add(config);
        //--------------------------------------------------------------
        String yaml = getYml(ordererOrgConfigSet);
        System.out.println(yaml);
    }

    private static void buildPeerOrgs() {
        PeerOrgConfigSet peerOrgConfigSet = new PeerOrgConfigSet();
        peerOrgConfigSet.setPeerOrgs(new ArrayList<Config>());
        //--------------------------------------------------------------
        Config config1 = new Config();
        config1.setEnableNodeOUs(true);
        config1.setName("Org1");
        config1.setDomain("org1.example.com");
        UnitInfo userUnitInfo = new UnitInfo();
        userUnitInfo.setCount(2);
        config1.setUsers(userUnitInfo);
        UnitInfo tmpUnitInfo = new UnitInfo();
        tmpUnitInfo.setCount(1);
        config1.setTemplate(tmpUnitInfo);
        peerOrgConfigSet.getPeerOrgs().add(config1);
        //--------------------------------------------------------------
        Config config2 = new Config();
        config2.setEnableNodeOUs(true);
        config2.setName("Org2");
        config2.setDomain("org2.example.com");
        userUnitInfo = new UnitInfo();
        userUnitInfo.setCount(2);
        config2.setUsers(userUnitInfo);
        tmpUnitInfo = new UnitInfo();
        tmpUnitInfo.setCount(2);
        config2.setTemplate(tmpUnitInfo);
        peerOrgConfigSet.getPeerOrgs().add(config2);
        //--------------------------------------------------------------
        Config config3 = new Config();
        config3.setEnableNodeOUs(true);
        config3.setName("Org3");
        config3.setDomain("org3.example.com");
        userUnitInfo = new UnitInfo();
        userUnitInfo.setCount(2);
        config3.setUsers(userUnitInfo);
        tmpUnitInfo = new UnitInfo();
        tmpUnitInfo.setCount(3);
        config3.setTemplate(tmpUnitInfo);
        peerOrgConfigSet.getPeerOrgs().add(config3);
        //--------------------------------------------------------------
        String yaml = getYml(peerOrgConfigSet);
        System.out.println(yaml);
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
}




