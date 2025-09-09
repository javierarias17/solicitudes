package co.com.pragma.consumer.aws.dto;

import java.util.List;

public record CapacityOutDTO(
        Long statusId,
        String status,
        List<PaymentPlanDTO> paymentPlans
) {}