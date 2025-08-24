package co.com.pragma.model.application;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    private Long id;
    private Double amount;
    private Long term;
    private String email;
    private Long statusId;
    private Long loanTypeId;
}
