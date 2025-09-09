package co.com.pragma.model.outport;

import co.com.pragma.model.capacity.calculation.PaymentPlan;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AwsQueueGateway {
    Mono<String> sendNotification(String email, String statusName);
    Mono<String> sendLoanCapacityPaymentPlanQueue(Long applicationId, String email, List<PaymentPlan> lstPaymentPlan);
}
