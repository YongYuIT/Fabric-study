package docker;

import lombok.Data;

import java.util.List;

@Data
public class BaseService {
    private String container_name;
    private List<String> volumes;
    private List<String> ports;
    private String image;
    private List<String> environment;
    private String working_dir;
    private String command;
    private List<String> extra_hosts;
}
