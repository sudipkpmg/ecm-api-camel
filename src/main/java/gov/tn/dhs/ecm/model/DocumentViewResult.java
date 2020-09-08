package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentViewResult {

    @JsonProperty("previewUrl")
    private String previewUrl;

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

}


