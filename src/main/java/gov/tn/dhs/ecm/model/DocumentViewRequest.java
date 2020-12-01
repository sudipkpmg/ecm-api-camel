package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentViewRequest {

    @JsonProperty("appUserIdId")
    private String appUserId;

    @JsonProperty("documentId")
    private String documentId;

    @JsonProperty("logonUserId")
    private String logonUserId;

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getLogonUserId() {
        return logonUserId;
    }

    public void setLogonUserId(String logonUserId) {
        this.logonUserId = logonUserId;
    }
}

