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

        System.out.println(">>> [RestConsumer] Request body: " + request);

        return client
                .post()
                .uri(USERS_BY_IDENTITY_DOCUMENTS)
                .bodyValue(request)
                .exchangeToMono(response -> {
                    System.out.println(">>> [RestConsumer] Response status: " + response.statusCode());

                    // loguear el body crudo como String
                    return response.bodyToMono(String.class)
                            .doOnNext(raw -> System.out.println(">>> [RestConsumer] Raw response body: " + raw))
                            // mapear de nuevo el string a tu UserResponse
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

        /*ObjectRequest request = ObjectRequest.builder()
                .lstIdentityDocument(lstIdentityDocument)
                .build();
        return client
                .post()
                .uri(USERS_BY_IDENTITY_DOCUMENTS)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .map(UserResponse::getLstUserDTO)
                .map(userDTOMapper::toModelList);*/
    }
}
