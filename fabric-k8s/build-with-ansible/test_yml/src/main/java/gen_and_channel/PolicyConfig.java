package gen_and_channel;

import lombok.Data;

@Data
public class PolicyConfig {
    private Rule Readers;
    private Rule Writers;
    private Rule Admins;
    private Rule BlockValidation;

    @Data
    public static class Rule {
        private String Type;
        private String Rule;
    }

}

