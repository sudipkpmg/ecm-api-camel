package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FolderCreationRequest {

    @JsonProperty("citizen_metadata")
    private CitizenMetadata citizenMetadata;

    public CitizenMetadata getCitizenMetadata() {
        return citizenMetadata;
    }

    public void setCitizenMetadata(CitizenMetadata citizenMetadata) {
        this.citizenMetadata = citizenMetadata;
    }
}

