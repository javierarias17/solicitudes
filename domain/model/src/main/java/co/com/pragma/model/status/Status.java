package co.com.pragma.model.status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {

    private Long id;
    private String name;
    private String description;
}
