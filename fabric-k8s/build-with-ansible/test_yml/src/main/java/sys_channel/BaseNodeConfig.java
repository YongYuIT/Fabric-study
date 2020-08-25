package sys_channel;

import lombok.Data;

@Data
public class BaseNodeConfig {
    protected String Name;
    protected String ID;
    protected String MSPDir;
    protected PolicyConfig Policies;
}
