package co.com.pragma.config;

import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.config.AWSSecretsManagerConfig;
import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretsConfig {

    @Bean
    @Profile({"dev"})
    public GenericManagerAsync getSecretManager(@Value("${aws.region}") String region) {
        return new AWSSecretManagerConnectorAsync(getConfig(region));
    }

    private AWSSecretsManagerConfig getConfig(String region) {
        return AWSSecretsManagerConfig.builder()
                .region(Region.of(region))
                .cacheSize(2)
                .cacheSeconds(1800)
                .build();
    }
}
