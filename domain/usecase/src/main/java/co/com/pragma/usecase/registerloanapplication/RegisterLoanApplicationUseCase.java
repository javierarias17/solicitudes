package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.exceptions.TechnicalException;
import co.com.pragma.usecase.exceptions.ValidationException;
import co.com.pragma.usecase.registerloanapplication.inport.RegisterLoanApplicationUseCaseInPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase implements RegisterLoanApplicationUseCaseInPort {

    private final static Long PENDENT_STATUS=1L;

    private final ApplicationRepository applicationRepository;
    private final StatusRepository statusRepository;
    private final LoanTypeRepository loanTypeRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");


    private static final Logger LOGGER=Logger.getLogger("InfoLogging");

    @Override
    public Mono<Application> saveApplication(Application application) {
        return validateAll(application)
                .then(Mono.just(application)
                        .map(app -> {
                            app.setStatusId(PENDENT_STATUS);
                            app.setId(null);
                            return app;
                        }))
                .flatMap(applicationRepository::saveApplication)
                .onErrorResume(Exception.class, ex -> {
                    //Errores esperados
                    if (ex instanceof ValidationException) {
                        return Mono.error(ex);
                    }
                    // Errores inesperados
                    LOGGER.severe("Technical error: " + ex);
                    return Mono.error(new TechnicalException("Technical error: Unexpected error saving application. Please contact the administrator."));
                });
    }

    private Mono<Void> validateAll(Application application) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        if (application.getAmount() == null) {
            errors.put("amount", "Amount is required");
        }
        if (application.getTerm() == null) {
            errors.put("term", "Term is required");
        }
        if (application.getLoanTypeId() == null) {
            errors.put("loanTypeId", "Loan type is required");
        }
        if (application.getEmail() == null || application.getEmail().isBlank()) {
            errors.put("email", "Email is required");
        } else if (!EMAIL_PATTERN.matcher(application.getEmail()).matches()) {
            errors.put("email", "Invalid email format");
        }

        if (application.getIdentityDocument() == null || application.getIdentityDocument().isBlank()) {
            errors.put("identityDocument", "Identity document is required");
        }

        if (!errors.isEmpty()) {
            return Mono.error(new ValidationException(errors));
        }

        // Validaciones contra la BD
        return Mono.zip(
                loanTypeRepository.findById(application.getLoanTypeId()).hasElement(),
                statusRepository.findById(PENDENT_STATUS).hasElement()
        ).flatMap(results -> {
            boolean loanTypeExists = results.getT1();
            boolean statusExists = results.getT2();

            if (!loanTypeExists) errors.put("loanTypeId", "Loan type does not exist");
            if (!statusExists) errors.put("statusId", "Status does not exist");

            return errors.isEmpty() ? Mono.empty() : Mono.error(new ValidationException(errors));
        });
    }
}
