package epn.edu.ec.model.cake;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
public class UpdateCakeRequest {
    private String title;
    private String description;
}
