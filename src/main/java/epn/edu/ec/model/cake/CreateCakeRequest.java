package epn.edu.ec.model.cake;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CreateCakeRequest {
    private String title;
    private String description;
     
}
