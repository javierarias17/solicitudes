package co.com.pragma.consumer.app;

import co.com.pragma.consumer.app.mapper.UserDTOMapper;
import co.com.pragma.model.application.User;
import co.com.pragma.model.outport.AuthenticationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestConsumer implements AuthenticationGateway {

    @Qualifier("restClient")
    private final WebClient client;
    private final UserDTOMapper userDTOMapper;

    public static final String USERS_BY_IDENTITY_DOCUMENTS = "/api/v1/usuarios/porIdentificacionDocumentos";

    //@CircuitBreaker(name = "testPost")
    @Override
    public Mono<List<User>> getUsersByIdentityDocuments(List<String> lstIdentityDocument) {
        ObjectRequest request = ObjectRequest.builder()
                .lstIdentityDocument(lstIdentityDocument)
                .build();

        return client
                .post()
                .uri(USERS_BY_IDENTITY_DOCUMENTS)
                .bodyValue(request)
                .exchangeToMono(response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(raw -> Mono.justOrEmpty(
                                    client
                                            .post()
                                            .uri(USERS_BY_IDENTITY_DOCUMENTS)
                                            .bodyValue(request)
                                            .retrieve()
                                            .bodyToMono(UserResponse.class)
                            ))
                            .flatMap(mono -> mono);
                })
                .map(UserResponse::getLstUserDTO)
                .map(userDTOMapper::toModelList);
    }
}
