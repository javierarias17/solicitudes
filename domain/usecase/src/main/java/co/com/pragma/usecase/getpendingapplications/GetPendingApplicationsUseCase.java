package co.com.pragma.usecase.getpendingapplications;

import co.com.pragma.model.application.ApplicationSummary;
import co.com.pragma.model.application.User;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.outport.AuthenticationGateway;
import co.com.pragma.usecase.getpendingapplications.inport.GetPendingApplicationsUseCaseInPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetPendingApplicationsUseCase implements GetPendingApplicationsUseCaseInPort{

    private final ApplicationRepository applicationRepository;
    private final AuthenticationGateway authenticationGateway;

    @Override
    public Flux<ApplicationSummary> execute(int page, int size) {
        long offset = (long) page * size;

        return applicationRepository.findPendingApplicationsPaged(size, offset)
                .collectList()
                .flatMapMany(applications -> {

                    List<String> lstIdentityDocument = applications.stream()
                            .map(ApplicationSummary::getIdentityDocument)
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    return authenticationGateway.getUsersByIdentityDocuments(lstIdentityDocument)
                            .flatMapMany(users -> {

                                Map<String, User> usersByIdentityDocument = users.stream()
                                        .collect(Collectors.toMap(User::getIdentityDocument, u -> u));

                                return Flux.fromIterable(applications)
                                        .map(app -> {
                                            User user = usersByIdentityDocument.get(app.getIdentityDocument());
                                            if (user != null) {
                                                app.setFullName(user.getFirstName() + " " + user.getLastName());
                                                app.setBaseSalary(user.getBaseSalary());
                                            }
                                            return app;
                                        });
                            });
                });
    }




}
