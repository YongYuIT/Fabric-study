package docker;

import lombok.Data;

@Data
public class DockerConfig {
    private String version;
    private ServiceConfig services;
    private volumeConfig volumes;

    @Data
    public static class ServiceConfig {
        private BaseService REPLACED;
    }

    @Data
    public static class volumeConfig {
        private String REPLACED;
    }
}