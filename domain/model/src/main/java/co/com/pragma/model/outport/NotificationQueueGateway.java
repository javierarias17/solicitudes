package co.com.pragma.model.outport;

import reactor.core.publisher.Mono;

public interface NotificationQueueGateway {
    Mono<String> sendNotification(String email, String statusName);
}
