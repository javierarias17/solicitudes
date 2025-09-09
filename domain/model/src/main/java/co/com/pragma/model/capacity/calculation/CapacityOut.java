package co.com.pragma.model.capacity.calculation;

import java.util.List;

public record CapacityOut(
        Long statusId,
        String status,
        List<PaymentPlan> paymentPlans
) {}