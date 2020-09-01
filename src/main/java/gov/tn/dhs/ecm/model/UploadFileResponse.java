package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UploadFileResponse {

  @JsonProperty("Status")
  private String status;

  @JsonProperty("FileId")
  private String fileId;

}

