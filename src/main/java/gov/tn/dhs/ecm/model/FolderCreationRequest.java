package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FolderCreationRequest {

    @JsonProperty("citizen_metadata")
    private CitizenMetadata citizenMetadata;

}

