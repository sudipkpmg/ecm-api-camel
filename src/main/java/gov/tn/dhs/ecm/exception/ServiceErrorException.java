package gov.tn.dhs.ecm.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceErrorException extends RuntimeException  {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

}
