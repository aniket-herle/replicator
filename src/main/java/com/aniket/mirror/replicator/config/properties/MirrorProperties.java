package com.aniket.mirror.replicator.config.properties;


import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "mirror")
public class MirrorProperties {

  private List<String> enabledProviders;
}
