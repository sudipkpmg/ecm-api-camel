package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Query   {

  @JsonProperty("fileName")
  private String fileName;

  @JsonProperty("folderId")
  private String folderId;

  @JsonProperty("searchType")
  private String searchType;

  @JsonProperty("offset")
  private int offset;

  @JsonProperty("limit")
  private int limit;

  public Query() {
    offset = 0;
    limit = 20;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public String getSearchType() {
    return searchType;
  }

  public void setSearchType(String searchType) {
    this.searchType = searchType;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}

