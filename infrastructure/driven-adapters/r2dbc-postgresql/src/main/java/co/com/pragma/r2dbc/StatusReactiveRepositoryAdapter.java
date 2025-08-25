package co.com.pragma.r2dbc;

import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.r2dbc.entity.StatusEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StatusReactiveRepositoryAdapter extends ReactiveAdapterOperations<Status, StatusEntity,Long,StatusReactiveRepository
> implements StatusRepository {

    public StatusReactiveRepositoryAdapter(StatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Status.class));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }
}
