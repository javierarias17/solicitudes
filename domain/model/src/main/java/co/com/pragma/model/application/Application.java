package co.com.pragma.model.application;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    private Long id;
    private BigDecimal amount;
    private Long term;
    private String email;
    private Long statusId;
    private Long loanTypeId;
    private String identityDocument;
}
