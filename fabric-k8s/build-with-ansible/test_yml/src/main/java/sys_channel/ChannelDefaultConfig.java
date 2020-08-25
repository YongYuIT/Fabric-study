package sys_channel;

import lombok.Data;

@Data
public class ChannelDefaultConfig {

    private PolicyConfig Policies;
    private CapabilityConfig Capabilities;

    @Data
    public static class CapabilityConfig extends ChannelCapabilityConfig {

    }

    @Data
    public static class ChannelCapabilityConfig {
        protected boolean V1_4_3;
        protected boolean V1_3;
        protected boolean V1_1;
    }
}
