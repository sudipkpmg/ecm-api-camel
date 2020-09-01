package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileInfo   {

  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("fileName")
  private String fileName;

  @JsonProperty("itemType")
  private String itemType;

  @JsonProperty("citizenMetadata")
  private CitizenMetadata citizenMetadata;

}

