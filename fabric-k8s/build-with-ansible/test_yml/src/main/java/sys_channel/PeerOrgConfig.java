package sys_channel;

import lombok.Data;

import java.util.List;

@Data
public class PeerOrgConfig extends BaseNodeConfig {
    private List<AnchorConfig> AnchorPeers;

    @Data
    public static class AnchorConfig {
        private String Host;
        private int Port;
    }
}