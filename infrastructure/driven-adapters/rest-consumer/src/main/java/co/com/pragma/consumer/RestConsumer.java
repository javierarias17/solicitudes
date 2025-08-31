package co.com.pragma.consumer;

import co.com.pragma.consumer.mapper.UserDTOMapper;
import co.com.pragma.model.application.User;
import co.com.pragma.model.outport.AuthenticationGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestConsumer implements AuthenticationGateway {
    private final WebClient client;
    private final UserDTOMapper userDTOMapper;

    public static final String USERS_BY_IDENTITY_DOCUMENTS = "/api/v1/usuarios/porIdentificacionDocumentos";

    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
    public Mono<UserResponse> testGet() {
        return client
                .get()
                .retrieve()
                .bodyToMono(UserResponse.class);
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    @CircuitBreaker(name = "testPost")
    public Mono<List<UserResponse>> testPost() {
        ObjectRequest request = ObjectRequest.builder()
            .lstIdentityDocument(new ArrayList<>())
            .build();
        return client
                .post()
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToFlux(UserResponse.class).collectList();
    }


    @Override
    public Mono<List<User>> getUsersByIdentityDocuments(List<String> lstIdentityDocument) {
        ObjectRequest request = ObjectRequest.builder()
                .lstIdentityDocument(lstIdentityDocument)
                .build();
        return client
                .post()
                .uri(USERS_BY_IDENTITY_DOCUMENTS)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .map(UserResponse::getLstUserDTO)
                .map(userDTOMapper::toModelList);
    }
}
