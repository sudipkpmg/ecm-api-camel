package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FolderCreationSuccessResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("metadata_status")
    private String metadata_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetadata_status() {
        return metadata_status;
    }

    public void setMetadata_status(String metadata_status) {
        this.metadata_status = metadata_status;
    }

}

