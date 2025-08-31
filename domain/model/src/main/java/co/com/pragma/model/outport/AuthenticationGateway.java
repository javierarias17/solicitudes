package co.com.pragma.model.outport;

import co.com.pragma.model.application.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuthenticationGateway {

    Mono<List<User>> getUsersByIdentityDocuments(List<String> lstIdentityDocument);
}
