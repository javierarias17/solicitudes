package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("loan_type")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanTypeEntity {

    @Id
    @Column("application_id")
    private Long id;
    private String name;
    @Column("min_amount")
    private Double minAmount;
    @Column("max_amount")
    private Double maxAmount;
    @Column("interest_rate")
    private Double interestRate;
    @Column("automatic_validation")
    private Boolean automaticValidation;
}
