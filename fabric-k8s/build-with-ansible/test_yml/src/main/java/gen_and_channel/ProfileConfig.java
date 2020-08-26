package gen_and_channel;

import lombok.Data;

import java.util.List;

@Data
public class ProfileConfig {

    private ProfileConfigItem Profiles;

    @Data
    public static class ProfileConfigItem {
        private ChannelConfig OrdererGenesis;
        private ChannelConfig Channel;
    }

    @Data
    public static class ChannelConfig extends ChannelDefConfig {
        private OrderConfig Orderer;
        private ClientConfig Consortiums;
        private String Consortium;
        private ApplicationConfig Application;

    }

    @Data
    public static class ClientConfig {
        private SampleConsortiumConfig SampleConsortium;
    }

    @Data
    public static class ApplicationConfig extends ApplicationDefConfig {
        private List<PeerOrgConfig> Organizations;
    }

    @Data
    public static class SampleConsortiumConfig {
        private List<PeerOrgConfig> Organizations;
    }


    @Data
    public static class OrderConfig extends OrderDefConfig {
        private List<OrdererOrgConfig> Organizations;
        private CapabilityConfig Capabilities;
    }

    @Data
    public static class OrdererOrgConfig extends BaseNodeConfig {

    }

}
