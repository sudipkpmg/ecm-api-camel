package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SearchResult {

    @JsonProperty("complete")
    private String complete;

    @JsonProperty("fileData")
    private List<FileInfo> fileData;

}
