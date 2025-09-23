package co.com.pragma.config;

import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.pragma.r2dbc.config.PostgresqlConnectionProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataBasePropertiesConfig {

    @Bean
    @Primary
    @Profile({"dev"})
    public PostgresqlConnectionProperties postgresqlConnectionPropertiesDev(
            GenericManagerAsync secretManager,
            @Value("${aws.secrets.database.name}") String databaseSecretName,
            @Value("${adapters.r2dbc.host}") String host,
            @Value("${adapters.r2dbc.port}") Integer port,
            @Value("${adapters.r2dbc.database}") String database,
            @Value("${adapters.r2dbc.schema}") String schema) throws SecretException {

        RdsSecretDTO rdsSecretDTO = secretManager.getSecret(databaseSecretName, RdsSecretDTO.class).block();
        return new PostgresqlConnectionProperties(host, port, database, schema, rdsSecretDTO.username(), rdsSecretDTO.password());
    }
}
