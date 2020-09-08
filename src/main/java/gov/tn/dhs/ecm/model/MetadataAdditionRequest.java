package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetadataAdditionRequest {

    @JsonProperty("documentId")
    private String documentId;

    @JsonProperty("documentTypeMetadata")
    private DocumentTypeMetadata documentTypeMetadata;

    @JsonProperty("documentConfidentialityMetadata")
    private DocumentConfidentialityMetadata documentConfidentialityMetadata;

    @JsonProperty("documentVerificationMetadata")
    private DocumentVerificationMetadata documentVerificationMetadata;

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

    public DocumentTypeMetadata getDocumentTypeMetadata() {
        return documentTypeMetadata;
    }

    public void setDocumentTypeMetadata(DocumentTypeMetadata documentTypeMetadata) {
        this.documentTypeMetadata = documentTypeMetadata;
    }

    public DocumentConfidentialityMetadata getDocumentConfidentialityMetadata() {
        return documentConfidentialityMetadata;
    }

    public void setDocumentConfidentialityMetadata(DocumentConfidentialityMetadata documentConfidentialityMetadata) {
        this.documentConfidentialityMetadata = documentConfidentialityMetadata;
    }

    public DocumentVerificationMetadata getDocumentVerificationMetadata() {
        return documentVerificationMetadata;
    }

    public void setDocumentVerificationMetadata(DocumentVerificationMetadata documentVerificationMetadata) {
        this.documentVerificationMetadata = documentVerificationMetadata;
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
