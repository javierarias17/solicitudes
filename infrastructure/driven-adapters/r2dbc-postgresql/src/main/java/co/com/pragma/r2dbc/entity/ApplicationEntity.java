package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("application")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApplicationEntity {

    @Id
    @Column("application_id")
    private Long id;
    private Double amount;
    private Long term;
    private String email;
    @Column("status_id")
    private Long statusId;
    @Column("loan_type_id")
    private Long loanTypeId;
}
