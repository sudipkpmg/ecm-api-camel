package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentFaSnapCaseMetadata {

    @JsonProperty("case_number")
    private String caseNumber;

    @JsonProperty("application_number")
    private String applicationNumber;

    @JsonProperty("recipient_id")
    private String recipientId;

    @JsonProperty("signed_date")
    private LocalDate signedDate;

    @JsonProperty("county")
    private String county;

    @JsonProperty("verified")
    private String verified;

    @JsonProperty("verified_by")
    private String verifiedBy;

    @JsonProperty("verified_date")
    private String verifiedDate;

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(LocalDate signedDate) {
        this.signedDate = signedDate;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(String verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

}
