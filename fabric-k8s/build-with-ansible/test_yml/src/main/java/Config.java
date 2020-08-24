import lombok.Data;

import java.util.List;

@Data
public class Config {
    private String Name;
    private String Domain;
    private boolean EnableNodeOUs;
    private UnitInfo Template;
    private UnitInfo Users;
    private List<HostConfig> Specs;
}