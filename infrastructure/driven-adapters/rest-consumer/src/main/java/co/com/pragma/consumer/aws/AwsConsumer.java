package co.com.pragma.consumer.aws;

import co.com.pragma.consumer.aws.dto.CapacityInDTO;
import co.com.pragma.consumer.aws.dto.CapacityOutDTO;
import co.com.pragma.consumer.aws.mapper.CapacidadMapper;
import co.com.pragma.model.capacity.calculation.CapacityIn;
import co.com.pragma.model.capacity.calculation.CapacityOut;
import co.com.pragma.model.outport.CapacityLambdaGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AwsConsumer implements CapacityLambdaGateway {

    private final WebClient lambdaClient;
    private final CapacidadMapper capacidadMapper;
    private final ObjectMapper objectMapper;
    public static final String CAPACITY_CALCULATE = "/api/v1/calcular-capacidad";
    private static final Logger log = LoggerFactory.getLogger(AwsConsumer.class);

    public AwsConsumer(
            @Qualifier("awsClient") WebClient lambdaClient,
            CapacidadMapper capacidadMapper,
            ObjectMapper objectMapper
    ) {
        this.lambdaClient = lambdaClient;
        this.capacidadMapper = capacidadMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<CapacityOut> calculateCapacity(CapacityIn capacityIn) {
        CapacityInDTO dto = capacidadMapper.toDTO(capacityIn);

        return lambdaClient
                .post()
                .uri(CAPACITY_CALCULATE)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSubscribe(sub -> {
                    try {
                        log.info("📤 Enviando a Lambda: {}", objectMapper.writeValueAsString(dto));
                    } catch (Exception e) {
                        log.warn("No se pudo serializar el request para logging", e);
                    }
                })
                .doOnNext(resp -> log.info("📥 Respuesta cruda de Lambda: {}", resp))
                .map(json -> {
                    try {
                        CapacityOutDTO dtoResp = objectMapper.readValue(json, CapacityOutDTO.class);
                        return capacidadMapper.toModel(dtoResp);
                    } catch (Exception e) {
                        throw new RuntimeException("Error parseando respuesta Lambda", e);
                    }
                });

    }
}