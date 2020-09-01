package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Query   {

  @JsonProperty("fileName")
  private String fileName;

  @JsonProperty("folderId")
  private String folderId;

  @JsonProperty("searchType")
  private String searchType;

}

