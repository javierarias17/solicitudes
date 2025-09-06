package co.com.pragma.sqs.sender;

import co.com.pragma.model.outport.NotificationQueueGateway;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationQueueGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .doOnNext(request -> log.info("Enviando mensaje a SQS. QueueUrl={}, Body={}",
                        request.queueUrl(), request.messageBody()))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Mensaje enviado correctamente. MessageId={}",
                        response.messageId()))
                .doOnError(error -> log.error("Error al enviar mensaje a SQS", error))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<String> sendNotification(String email, String statusName) {
        return send(String.format("{\"email\": \"%s\", \"status\": \"%s\"}", email, statusName));
    }

}
