package sys_channel;

import lombok.Data;

import java.util.List;

@Data
public class OrderDefConfig {
    private String OrdererType;
    private List<String> Addresses;
    private String BatchTimeout;
    private BatchSizeConfig BatchSize;
    private Object Organizations;
    private PolicyConfig Policies;

    @Data
    public static class BatchSizeConfig {
        private int MaxMessageCount;
        private String AbsoluteMaxBytes;
        private String PreferredMaxBytes;
    }
}
