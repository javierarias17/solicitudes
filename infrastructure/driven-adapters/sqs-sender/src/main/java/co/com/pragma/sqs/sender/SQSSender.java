package co.com.pragma.sqs.sender;

import co.com.pragma.model.capacity.calculation.PaymentPlan;
import co.com.pragma.model.outport.AwsQueueGateway;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements AwsQueueGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    public static final String NOTIFICATION_QUEUE = "/notificationQueue";
    public static final String LOAN_CAPACITY_PAYMENT_PLAN_QUEUE = "/loanCapacityPaymentPlanQueue";


    public Mono<String> send(String message, String url) {
        return Mono.fromCallable(() -> buildRequest(message, url))
                .doOnNext(request -> log.info("Enviando mensaje a SQS. QueueUrl={}, Body={}",
                        request.queueUrl(), request.messageBody()))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Mensaje enviado correctamente. MessageId={}",
                        response.messageId()))
                .doOnError(error -> log.error("Error al enviar mensaje a SQS", error))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message, String url) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl()+url)
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<String> sendNotification(String email, String statusName) {
        return send(String.format("{\"email\": \"%s\", \"status\": \"%s\"}", email, statusName), NOTIFICATION_QUEUE);
    }

    @Override
    public Mono<String> sendLoanCapacityPaymentPlanQueue(Long applicationId, String email, List<PaymentPlan> lstPaymentPlan) {
        String paymentPlansJson = lstPaymentPlan.stream()
                .map(plan -> String.format(java.util.Locale.US,
                        "{\"month\": %d, \"installment\": %.2f, \"principalPayment\": %.2f, \"interestRate\": %.2f, \"remainingBalance\": %.2f}",
                        plan.month(),
                        plan.installment(),
                        plan.principalPayment(),
                        plan.interestRate(),
                        plan.remainingBalance()
                ))
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        String jsonMessage = String.format(
                "{\"id\": \"%s\", \"email\": \"%s\", \"paymentPlans\": [%s]}",
                applicationId,
                email,
                paymentPlansJson
        );

        return send(jsonMessage, LOAN_CAPACITY_PAYMENT_PLAN_QUEUE);
    }

}
