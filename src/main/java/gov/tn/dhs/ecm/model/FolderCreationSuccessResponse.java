package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FolderCreationSuccessResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("metadata_status")
    private String metadata_status;

}

