package co.com.pragma.model.outport;

import co.com.pragma.model.capacity.calculation.CapacityIn;
import co.com.pragma.model.capacity.calculation.CapacityOut;
import reactor.core.publisher.Mono;

public interface CapacityLambdaGateway {
    Mono<CapacityOut> calculateCapacity(CapacityIn capacityRequest);
}
