package sys_channel;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class BuildTools {
    public static void buildOrdererGenesis() throws Exception {

        ProfileConfig profileConfig = new ProfileConfig();
        profileConfig.setProfiles(new ProfileConfig.ProfileConfigItem());

        profileConfig.getProfiles().setOrdererGenesis(new ProfileConfig.ChannelConfig());
        profileConfig.getProfiles().getOrdererGenesis().setConsortiums(new ProfileConfig.ClientConfig());
        profileConfig.getProfiles().getOrdererGenesis().setOrderer(new ProfileConfig.OrderConfig());
        profileConfig.getProfiles().getOrdererGenesis().setCapabilities(new CapabilityConfig());

        profileConfig.getProfiles().getOrdererGenesis().getCapabilities().setV1_1("true");
        profileConfig.getProfiles().getOrdererGenesis().getCapabilities().setV1_4_3("false");
        profileConfig.getProfiles().getOrdererGenesis().getCapabilities().setV1_3("false");

        profileConfig.getProfiles().getOrdererGenesis().setPolicies(new PolicyConfig());
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().setWriters(new PolicyConfig.Rule());
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().setAdmins(new PolicyConfig.Rule());
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().setReaders(new PolicyConfig.Rule());
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().getReaders().setRule("\"ANY Readers\"");
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().getReaders().setType("ImplicitMeta");
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().getWriters().setType("ImplicitMeta");
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().getWriters().setRule("\"ANY Writers\"");
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().getAdmins().setType("ImplicitMeta");
        profileConfig.getProfiles().getOrdererGenesis().getPolicies().getAdmins().setRule("\"MAJORITY Admins\"");

        profileConfig.getProfiles().getOrdererGenesis().getConsortiums().setSampleConsortium(new ProfileConfig.SampleConsortiumConfig());
        profileConfig.getProfiles().getOrdererGenesis().getConsortiums().getSampleConsortium().setOrganizations(new ArrayList<PeerOrgConfig>());
        List<PeerOrgConfig> peerOrgs = profileConfig.getProfiles().getOrdererGenesis().getConsortiums().getSampleConsortium().getOrganizations();

        PeerOrgConfig peerOrg;
        peerOrg = new PeerOrgConfig();
        peerOrg.setID("Org1MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org1.example.com/msp");
        peerOrg.setName("Org1MSP");
        peerOrg.setAnchorPeers(new ArrayList<PeerOrgConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new PeerOrgConfig.AnchorConfig());
        peerOrg.getAnchorPeers().get(0).setHost("peer0.org1.example.com");
        peerOrg.getAnchorPeers().get(0).setPort(7051);
        peerOrg.setPolicies(new PolicyConfig());
        peerOrg.getPolicies().setAdmins(new PolicyConfig.Rule());
        peerOrg.getPolicies().getAdmins().setType("Signature");
        peerOrg.getPolicies().getAdmins().setRule("\"OR('Org1MSP.admin')\"");
        peerOrg.getPolicies().setWriters(new PolicyConfig.Rule());
        peerOrg.getPolicies().getWriters().setType("Signature");
        peerOrg.getPolicies().getWriters().setRule("\"OR('Org1MSP.admin', 'Org1MSP.client')\"");
        peerOrg.getPolicies().setReaders(new PolicyConfig.Rule());
        peerOrg.getPolicies().getReaders().setType("Signature");
        peerOrg.getPolicies().getReaders().setRule("\"OR('Org1MSP.admin', 'Org1MSP.peer', 'Org1MSP.client')\"");
        peerOrgs.add(peerOrg);

        peerOrg = new PeerOrgConfig();
        peerOrg.setID("Org2MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org2.example.com/msp");
        peerOrg.setName("Org2MSP");
        peerOrg.setAnchorPeers(new ArrayList<PeerOrgConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new PeerOrgConfig.AnchorConfig());
        peerOrg.getAnchorPeers().get(0).setHost("peer0.org2.example.com");
        peerOrg.getAnchorPeers().get(0).setPort(9051);
        peerOrg.setPolicies(new PolicyConfig());
        peerOrg.getPolicies().setAdmins(new PolicyConfig.Rule());
        peerOrg.getPolicies().getAdmins().setType("Signature");
        peerOrg.getPolicies().getAdmins().setRule("\"OR('Org2MSP.admin')\"");
        peerOrg.getPolicies().setWriters(new PolicyConfig.Rule());
        peerOrg.getPolicies().getWriters().setType("Signature");
        peerOrg.getPolicies().getWriters().setRule("\"OR('Org2MSP.admin', 'Org2MSP.client')\"");
        peerOrg.getPolicies().setReaders(new PolicyConfig.Rule());
        peerOrg.getPolicies().getReaders().setType("Signature");
        peerOrg.getPolicies().getReaders().setRule("\"OR('Org2MSP.admin', 'Org2MSP.peer', 'Org2MSP.client')\"");
        peerOrgs.add(peerOrg);

        peerOrg = new PeerOrgConfig();
        peerOrg.setID("Org3MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org3.example.com/msp");
        peerOrg.setName("Org3MSP");
        peerOrg.setAnchorPeers(new ArrayList<PeerOrgConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new PeerOrgConfig.AnchorConfig());
        peerOrg.getAnchorPeers().get(0).setHost("peer0.org3.example.com");
        peerOrg.getAnchorPeers().get(0).setPort(11051);
        peerOrg.setPolicies(new PolicyConfig());
        peerOrg.getPolicies().setAdmins(new PolicyConfig.Rule());
        peerOrg.getPolicies().getAdmins().setType("Signature");
        peerOrg.getPolicies().getAdmins().setRule("\"OR('Org3MSP.admin')\"");
        peerOrg.getPolicies().setWriters(new PolicyConfig.Rule());
        peerOrg.getPolicies().getWriters().setType("Signature");
        peerOrg.getPolicies().getWriters().setRule("\"OR('Org3MSP.admin', 'Org3MSP.client')\"");
        peerOrg.getPolicies().setReaders(new PolicyConfig.Rule());
        peerOrg.getPolicies().getReaders().setType("Signature");
        peerOrg.getPolicies().getReaders().setRule("\"OR('Org3MSP.admin', 'Org3MSP.peer', 'Org3MSP.client')\"");
        peerOrgs.add(peerOrg);

        profileConfig.getProfiles().getOrdererGenesis().getOrderer().setCapabilities(new CapabilityConfig());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getCapabilities().setV1_1("false");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getCapabilities().setV1_4_2("true");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().setOrganizations(new ArrayList<ProfileConfig.OrdererOrgConfig>());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().setOrdererType("solo");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().setAddresses(new ArrayList<String>());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getAddresses().add("orderer.example.com:7050");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().setBatchTimeout("2s");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().setBatchSize(new OrderDefConfig.BatchSizeConfig());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getBatchSize().setAbsoluteMaxBytes("99 MB");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getBatchSize().setMaxMessageCount(10);
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getBatchSize().setPreferredMaxBytes("512 KB");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().setPolicies(new PolicyConfig());

        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().setReaders(new PolicyConfig.Rule());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().setWriters(new PolicyConfig.Rule());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().setAdmins(new PolicyConfig.Rule());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().setBlockValidation(new PolicyConfig.Rule());
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getAdmins().setType("ImplicitMeta");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getAdmins().setRule("\"MAJORITY Admins\"");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getReaders().setType("ImplicitMeta");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getReaders().setRule("\"ANY Readers\"");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getWriters().setType("ImplicitMeta");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getWriters().setRule("\"ANY Writers\"");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getBlockValidation().setType("ImplicitMeta");
        profileConfig.getProfiles().getOrdererGenesis().getOrderer().getPolicies().getBlockValidation().setRule("\"ANY Writers\"");

        List<ProfileConfig.OrdererOrgConfig> ordererOrgConfigs = profileConfig.getProfiles().getOrdererGenesis().getOrderer().getOrganizations();
        ProfileConfig.OrdererOrgConfig ordererOrgConfig = new ProfileConfig.OrdererOrgConfig();
        ordererOrgConfig.setID("OrdererMSP");
        ordererOrgConfig.setMSPDir("crypto-config/ordererOrganizations/example.com/msp");
        ordererOrgConfig.setName("OrdererOrg");
        ordererOrgConfig.setPolicies(new PolicyConfig());
        ordererOrgConfig.getPolicies().setAdmins(new PolicyConfig.Rule());
        ordererOrgConfig.getPolicies().getAdmins().setType("Signature");
        ordererOrgConfig.getPolicies().getAdmins().setRule("\"OR('OrdererMSP.admin')\"");
        ordererOrgConfig.getPolicies().setReaders(new PolicyConfig.Rule());
        ordererOrgConfig.getPolicies().getReaders().setRule("\"OR('OrdererMSP.member')\"");
        ordererOrgConfig.getPolicies().getReaders().setType("Signature");
        ordererOrgConfig.getPolicies().setWriters(new PolicyConfig.Rule());
        ordererOrgConfig.getPolicies().getWriters().setType("Signature");
        ordererOrgConfig.getPolicies().getWriters().setRule("\"OR('OrdererMSP.member')\"");
        ordererOrgConfigs.add(ordererOrgConfig);

        String yml = getYml(profileConfig);
        yml = yml.replace("''", "'");
        yml = yml.replace("'\"", "\"");
        yml = yml.replace("\"'", "\"");
        saveFile(yml, "configtx-genesis.yaml");
        System.out.println(yml);
    }

    public static void buildChannel() throws Exception {
        ProfileConfig profileConfig = new ProfileConfig();
        profileConfig.setProfiles(new ProfileConfig.ProfileConfigItem());

        profileConfig.getProfiles().setChannel(new ProfileConfig.ChannelConfig());
        profileConfig.getProfiles().getChannel().setConsortium("SampleConsortium");
        profileConfig.getProfiles().getChannel().setCapabilities(new CapabilityConfig());

        profileConfig.getProfiles().getChannel().getCapabilities().setV1_1("false");
        profileConfig.getProfiles().getChannel().getCapabilities().setV1_4_3("true");
        profileConfig.getProfiles().getChannel().getCapabilities().setV1_3("false");

        profileConfig.getProfiles().getChannel().setPolicies(new PolicyConfig());
        profileConfig.getProfiles().getChannel().getPolicies().setWriters(new PolicyConfig.Rule());
        profileConfig.getProfiles().getChannel().getPolicies().setAdmins(new PolicyConfig.Rule());
        profileConfig.getProfiles().getChannel().getPolicies().setReaders(new PolicyConfig.Rule());
        profileConfig.getProfiles().getChannel().getPolicies().getReaders().setRule("\"ANY Readers\"");
        profileConfig.getProfiles().getChannel().getPolicies().getReaders().setType("ImplicitMeta");
        profileConfig.getProfiles().getChannel().getPolicies().getWriters().setType("ImplicitMeta");
        profileConfig.getProfiles().getChannel().getPolicies().getWriters().setRule("\"ANY Writers\"");
        profileConfig.getProfiles().getChannel().getPolicies().getAdmins().setType("ImplicitMeta");
        profileConfig.getProfiles().getChannel().getPolicies().getAdmins().setRule("\"MAJORITY Admins\"");

        profileConfig.getProfiles().getChannel().setApplication(new ProfileConfig.ApplicationConfig());
        profileConfig.getProfiles().getChannel().getApplication().setPolicies(new PolicyConfig());
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().setWriters(new PolicyConfig.Rule());
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().setAdmins(new PolicyConfig.Rule());
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().setReaders(new PolicyConfig.Rule());
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().getReaders().setRule("\"ANY Readers\"");
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().getReaders().setType("ImplicitMeta");
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().getWriters().setType("ImplicitMeta");
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().getWriters().setRule("\"ANY Writers\"");
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().getAdmins().setType("ImplicitMeta");
        profileConfig.getProfiles().getChannel().getApplication().getPolicies().getAdmins().setRule("\"MAJORITY Admins\"");

        profileConfig.getProfiles().getChannel().getApplication().setCapabilities(new CapabilityConfig());
        profileConfig.getProfiles().getChannel().getApplication().getCapabilities().setV1_1("false");
        profileConfig.getProfiles().getChannel().getApplication().getCapabilities().setV1_4_3("true");
        profileConfig.getProfiles().getChannel().getApplication().getCapabilities().setV1_3("false");
        profileConfig.getProfiles().getChannel().getApplication().getCapabilities().setV1_2("false");


        profileConfig.getProfiles().getChannel().getApplication().setOrganizations(new ArrayList<PeerOrgConfig>());
        List<PeerOrgConfig> peerOrgs = profileConfig.getProfiles().getChannel().getApplication().getOrganizations();

        PeerOrgConfig peerOrg;
        peerOrg = new PeerOrgConfig();
        peerOrg.setID("Org1MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org1.example.com/msp");
        peerOrg.setName("Org1MSP");
        peerOrg.setAnchorPeers(new ArrayList<PeerOrgConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new PeerOrgConfig.AnchorConfig());
        peerOrg.getAnchorPeers().get(0).setHost("peer0.org1.example.com");
        peerOrg.getAnchorPeers().get(0).setPort(7051);
        peerOrg.setPolicies(new PolicyConfig());
        peerOrg.getPolicies().setAdmins(new PolicyConfig.Rule());
        peerOrg.getPolicies().getAdmins().setType("Signature");
        peerOrg.getPolicies().getAdmins().setRule("\"OR('Org1MSP.admin')\"");
        peerOrg.getPolicies().setWriters(new PolicyConfig.Rule());
        peerOrg.getPolicies().getWriters().setType("Signature");
        peerOrg.getPolicies().getWriters().setRule("\"OR('Org1MSP.admin', 'Org1MSP.client')\"");
        peerOrg.getPolicies().setReaders(new PolicyConfig.Rule());
        peerOrg.getPolicies().getReaders().setType("Signature");
        peerOrg.getPolicies().getReaders().setRule("\"OR('Org1MSP.admin', 'Org1MSP.peer', 'Org1MSP.client')\"");
        peerOrgs.add(peerOrg);

        peerOrg = new PeerOrgConfig();
        peerOrg.setID("Org2MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org2.example.com/msp");
        peerOrg.setName("Org2MSP");
        peerOrg.setAnchorPeers(new ArrayList<PeerOrgConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new PeerOrgConfig.AnchorConfig());
        peerOrg.getAnchorPeers().get(0).setHost("peer0.org2.example.com");
        peerOrg.getAnchorPeers().get(0).setPort(9051);
        peerOrg.setPolicies(new PolicyConfig());
        peerOrg.getPolicies().setAdmins(new PolicyConfig.Rule());
        peerOrg.getPolicies().getAdmins().setType("Signature");
        peerOrg.getPolicies().getAdmins().setRule("\"OR('Org2MSP.admin')\"");
        peerOrg.getPolicies().setWriters(new PolicyConfig.Rule());
        peerOrg.getPolicies().getWriters().setType("Signature");
        peerOrg.getPolicies().getWriters().setRule("\"OR('Org2MSP.admin', 'Org2MSP.client')\"");
        peerOrg.getPolicies().setReaders(new PolicyConfig.Rule());
        peerOrg.getPolicies().getReaders().setType("Signature");
        peerOrg.getPolicies().getReaders().setRule("\"OR('Org2MSP.admin', 'Org2MSP.peer', 'Org2MSP.client')\"");
        peerOrgs.add(peerOrg);

        peerOrg = new PeerOrgConfig();
        peerOrg.setID("Org3MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org3.example.com/msp");
        peerOrg.setName("Org3MSP");
        peerOrg.setAnchorPeers(new ArrayList<PeerOrgConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new PeerOrgConfig.AnchorConfig());
        peerOrg.getAnchorPeers().get(0).setHost("peer0.org3.example.com");
        peerOrg.getAnchorPeers().get(0).setPort(11051);
        peerOrg.setPolicies(new PolicyConfig());
        peerOrg.getPolicies().setAdmins(new PolicyConfig.Rule());
        peerOrg.getPolicies().getAdmins().setType("Signature");
        peerOrg.getPolicies().getAdmins().setRule("\"OR('Org3MSP.admin')\"");
        peerOrg.getPolicies().setWriters(new PolicyConfig.Rule());
        peerOrg.getPolicies().getWriters().setType("Signature");
        peerOrg.getPolicies().getWriters().setRule("\"OR('Org3MSP.admin', 'Org3MSP.client')\"");
        peerOrg.getPolicies().setReaders(new PolicyConfig.Rule());
        peerOrg.getPolicies().getReaders().setType("Signature");
        peerOrg.getPolicies().getReaders().setRule("\"OR('Org3MSP.admin', 'Org3MSP.peer', 'Org3MSP.client')\"");
        peerOrgs.add(peerOrg);


        String yml = getYml(profileConfig);
        yml = yml.replace("''", "'");
        yml = yml.replace("'\"", "\"");
        yml = yml.replace("\"'", "\"");
        saveFile(yml, "configtx-channel.yaml");
        System.out.println(yml);
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
