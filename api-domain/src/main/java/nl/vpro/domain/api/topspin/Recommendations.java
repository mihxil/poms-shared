package nl.vpro.domain.api.topspin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recommendations {
    private List<Recommendation> recommendations = new ArrayList<>();
}
