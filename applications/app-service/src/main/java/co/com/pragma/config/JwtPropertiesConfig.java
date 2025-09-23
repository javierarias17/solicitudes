package co.com.pragma.config;

import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.pragma.api.dto.JwtSecretDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class JwtPropertiesConfig {
    @Bean
    @Profile({"dev"})
    public JwtSecretDTO jwtSecretDTODev(
            GenericManagerAsync secretManager,
            @Value("${aws.secrets.jwt.name}") String jwtSecretName) throws SecretException {

        return secretManager.getSecret(jwtSecretName, JwtSecretDTO.class).block();
    }

    @Bean
    @Profile({"local"})
    public JwtSecretDTO jwtSecretDTOLocal(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") String expiration) {
        return new JwtSecretDTO(secret, expiration);
    }
}
