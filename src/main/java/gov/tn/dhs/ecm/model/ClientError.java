package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientError {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

}
