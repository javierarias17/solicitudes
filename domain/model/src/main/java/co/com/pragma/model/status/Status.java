package co.com.pragma.model.status;
import lombok.*;
//import lombok.NoArgsConstructor;


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
