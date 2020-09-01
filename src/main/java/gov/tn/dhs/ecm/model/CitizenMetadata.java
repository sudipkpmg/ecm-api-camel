package gov.tn.dhs.ecm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CitizenMetadata   {

    @JsonProperty("logon_user_id")
    private String logonUserId;

    @JsonProperty("mpi_id")
    private String mpiId;

    @JsonProperty("sys_id")
    private String sysId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("dob")
    private LocalDate dob;

    @JsonProperty("ssn4")
    private String ssn4;

}

