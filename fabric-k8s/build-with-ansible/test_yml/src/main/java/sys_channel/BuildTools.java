package sys_channel;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class BuildTools {
    public static void buildSysChannel() throws Exception {

        ProfileConfig profileConfig = new ProfileConfig();
        profileConfig.setProfiles(new ProfileConfig.ProfileConfigItem());

        profileConfig.getProfiles().setSystemChannelConfig(new ProfileConfig.ChannelConfig());
        profileConfig.getProfiles().getSystemChannelConfig().setConsortiums(new ProfileConfig.ClientConfig());
        profileConfig.getProfiles().getSystemChannelConfig().setOrderer(new ProfileConfig.OrderConfig());
        profileConfig.getProfiles().getSystemChannelConfig().setCapabilities(new ChannelDefaultConfig.CapabilityConfig());

        profileConfig.getProfiles().getSystemChannelConfig().getCapabilities().setV1_1(true);
        profileConfig.getProfiles().getSystemChannelConfig().getCapabilities().setV1_4_3(true);
        profileConfig.getProfiles().getSystemChannelConfig().getCapabilities().setV1_3(true);

        profileConfig.getProfiles().getSystemChannelConfig().setPolicies(new PolicyConfig());
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().setWriters(new PolicyConfig.Rule());
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().setAdmins(new PolicyConfig.Rule());
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().setReaders(new PolicyConfig.Rule());
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().getReaders().setRule("\"ANY Readers\"");
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().getReaders().setType("ImplicitMeta");
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().getWriters().setType("ImplicitMeta");
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().getWriters().setRule("\"ANY Writers\"");
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().getAdmins().setType("ImplicitMeta");
        profileConfig.getProfiles().getSystemChannelConfig().getPolicies().getAdmins().setRule("\"MAJORITY Admins\"");

        profileConfig.getProfiles().getSystemChannelConfig().getConsortiums().setSampleConsortium(new ProfileConfig.SampleConsortiumConfig());
        profileConfig.getProfiles().getSystemChannelConfig().getConsortiums().getSampleConsortium().setOrganizations(new ArrayList<ProfileConfig.PeerOrgConfig>());
        List<ProfileConfig.PeerOrgConfig> peerOrgs = profileConfig.getProfiles().getSystemChannelConfig().getConsortiums().getSampleConsortium().getOrganizations();

        ProfileConfig.PeerOrgConfig peerOrg;
        peerOrg = new ProfileConfig.PeerOrgConfig();
        peerOrg.setID("Org1MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org1.example.com/msp");
        peerOrg.setName("Org1MSP");
        peerOrg.setAnchorPeers(new ArrayList<ProfileConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new ProfileConfig.AnchorConfig());
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

        peerOrg = new ProfileConfig.PeerOrgConfig();
        peerOrg.setID("Org2MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org2.example.com/msp");
        peerOrg.setName("Org2MSP");
        peerOrg.setAnchorPeers(new ArrayList<ProfileConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new ProfileConfig.AnchorConfig());
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

        peerOrg = new ProfileConfig.PeerOrgConfig();
        peerOrg.setID("Org3MSP");
        peerOrg.setMSPDir("crypto-config/peerOrganizations/org3.example.com/msp");
        peerOrg.setName("Org3MSP");
        peerOrg.setAnchorPeers(new ArrayList<ProfileConfig.AnchorConfig>());
        peerOrg.getAnchorPeers().add(new ProfileConfig.AnchorConfig());
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

        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().setCapabilities(new ProfileConfig.OrdererCapabilityConfig());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getCapabilities().setV1_1(true);
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getCapabilities().setV1_4_2(true);
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().setOrganizations(new ArrayList<ProfileConfig.OrdererOrgConfig>());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().setOrdererType("solo");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().setAddresses(new ArrayList<String>());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getAddresses().add("orderer.example.com:7050");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().setBatchTimeout("2s");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().setBatchSize(new OrderDefConfig.BatchSizeConfig());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getBatchSize().setAbsoluteMaxBytes("99 MB");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getBatchSize().setMaxMessageCount(10);
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getBatchSize().setPreferredMaxBytes("512 KB");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().setPolicies(new PolicyConfig());

        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().setReaders(new PolicyConfig.Rule());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().setWriters(new PolicyConfig.Rule());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().setAdmins(new PolicyConfig.Rule());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().setBlockValidation(new PolicyConfig.Rule());
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getAdmins().setType("ImplicitMeta");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getAdmins().setRule("\"MAJORITY Admins\"");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getReaders().setType("ImplicitMeta");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getReaders().setRule("\"ANY Readers\"");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getWriters().setType("ImplicitMeta");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getWriters().setRule("\"ANY Writers\"");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getBlockValidation().setType("ImplicitMeta");
        profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getPolicies().getBlockValidation().setRule("\"ANY Writers\"");

        List<ProfileConfig.OrdererOrgConfig> ordererOrgConfigs = profileConfig.getProfiles().getSystemChannelConfig().getOrderer().getOrganizations();
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
