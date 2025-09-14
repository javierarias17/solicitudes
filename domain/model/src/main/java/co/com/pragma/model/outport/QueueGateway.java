package co.com.pragma.model.outport;

import co.com.pragma.model.capacity.calculation.PaymentPlan;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface QueueGateway {
    Mono<String> sendNotificationQueue(String email, String statusName);
    Mono<String> sendLoanCapacityPaymentPlanQueue(Long applicationId, String email, List<PaymentPlan> lstPaymentPlan);
    Mono<String> sendApprovedLoansQueue(BigDecimal amount);
}
