package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileDownloadRequest   {

  @JsonProperty("fileId")
  private String fileId;

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }
}

