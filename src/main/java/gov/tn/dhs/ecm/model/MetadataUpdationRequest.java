package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetadataUpdationRequest {

    @JsonProperty("documentId")
    private String documentId;

    @JsonProperty("caseFaSnapMetadata")
    private DocumentFaSnapCaseMetadata documentFaSnapCaseMetadata;

    @JsonProperty("caseChildCareMetadata")
    private DocumentChildCareCaseMetadata documentChildCareCaseMetadata;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public DocumentFaSnapCaseMetadata getDocumentFaSnapCaseMetadata() {
        return documentFaSnapCaseMetadata;
    }

    public void setDocumentFaSnapCaseMetadata(DocumentFaSnapCaseMetadata documentFaSnapCaseMetadata) {
        this.documentFaSnapCaseMetadata = documentFaSnapCaseMetadata;
    }

    public DocumentChildCareCaseMetadata getDocumentChildCareCaseMetadata() {
        return documentChildCareCaseMetadata;
    }

    public void setDocumentChildCareCaseMetadata(DocumentChildCareCaseMetadata documentChildCareCaseMetadata) {
        this.documentChildCareCaseMetadata = documentChildCareCaseMetadata;
    }

}
