package org.example.hominganimal.infrastructure.ezviz;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ezviz")
public class EzvizProperties {
    private String appKey;
    private String appSecret;
    private String apiBaseUrl = "https://open.ezviz.com";
    private Long tokenTl=604800L;
}
