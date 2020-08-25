package sys_channel;

import lombok.Data;

import java.util.List;

@Data
public class ProfileConfig {

    private ProfileConfigItem Profiles;

    @Data
    public static class ProfileConfigItem {
        private ChannelConfig SystemChannelConfig;
    }

    @Data
    public static class ChannelConfig extends ChannelDefaultConfig {
        private OrderConfig Orderer;
        private ClientConfig Consortiums;
    }

    @Data
    public static class ClientConfig {
        private SampleConsortiumConfig SampleConsortium;
    }

    @Data
    public static class SampleConsortiumConfig {
        private List<PeerOrgConfig> Organizations;
    }


    @Data
    public static class OrderConfig extends OrderDefConfig {
        private List<OrdererOrgConfig> Organizations;
        private OrdererCapabilityConfig Capabilities;
    }

    @Data
    public static class OrdererOrgConfig extends BaseNodeConfig {

    }

    @Data
    public static class PeerOrgConfig extends BaseNodeConfig {
        private List<AnchorConfig> AnchorPeers;
    }

    @Data
    public static class AnchorConfig {
        private String Host;
        private int Port;
    }

    @Data
    public static class OrdererCapabilityConfig {
        private boolean V1_4_2;
        private boolean V1_1;
    }
}
