package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.Metadata;
import com.eclipsesource.json.JsonObject;
import gov.tn.dhs.ecm.config.AppProperties;
import gov.tn.dhs.ecm.exception.ServiceErrorException;
import gov.tn.dhs.ecm.model.CitizenMetadata;
import gov.tn.dhs.ecm.model.FolderCreationSuccessResponse;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CreateFolderService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(CreateFolderService.class);

    private final ConnectionHelper connectionHelper;

    private final AppProperties appProperties;

    public CreateFolderService(ConnectionHelper connectionHelper, AppProperties appProperties) {
        this.connectionHelper = connectionHelper;
        this.appProperties = appProperties;
    }

    public void createFolder(Exchange exchange) throws ServiceErrorException {
        try {
            BoxDeveloperEditionAPIConnection api = connectionHelper.getBoxDeveloperEditionAPIConnection();

            CitizenMetadata citizenMetadata = exchange.getIn().getBody(CitizenMetadata.class);
            logger.info(citizenMetadata.toString());
            String firstName = citizenMetadata.getFirstName();
            String lastName = citizenMetadata.getLastName();
            String ssnOrTaxId = citizenMetadata.getSysId();
            String sysId = citizenMetadata.getSysId();
            String mpiId = citizenMetadata.getMpiId();
            String logonUserId = citizenMetadata.getLogonUserId();
            LocalDate dateOfBirth = citizenMetadata.getDob();
            String dob = null;
            if (dateOfBirth != null) {
                String ts = "T00:00:00-00:00";
                dob = String.format("%s%s", dateOfBirth.toString(), ts);
            }

            String parentFolderId = appProperties.getParentFolderID();
            BoxFolder parentFolder = new BoxFolder(api, parentFolderId);

            String folderName = (mpiId != null ? mpiId : sysId);
            if (folderName == null) {
                setupError("400", "Some of the parameters are missing or not valid");
            }
            BoxFolder.Info childFolderInfo = parentFolder.createFolder(folderName);
            String folderID = childFolderInfo.getID();

            FolderCreationSuccessResponse folderCreationSuccessResponse = new FolderCreationSuccessResponse();
            folderCreationSuccessResponse.setId(folderID);
            BoxFolder boxFolder = new BoxFolder(api, folderID);
            JsonObject jsonObject = new JsonObject()
                    .add("FirstName", firstName)
                    .add("LastName", lastName)
                    .add("last4ofssn", ssnOrTaxId)
                    .add("sysid", sysId)
                    .add("logonuserid", logonUserId)
                    .add("mpiid", mpiId)
                    ;
            if (dob != null) {
                jsonObject.add("dob1", dob);
            }
            Metadata metadata = new Metadata(jsonObject);
            try {
                boxFolder.createMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope(), metadata);
                folderCreationSuccessResponse.setMetadata_status("metadata successfully applied");
            } catch (Exception e) {
                folderCreationSuccessResponse.setMetadata_status("metadata could not be applied");
            }

            createSubFolder(api, "notifications", boxFolder, metadata);
            createSubFolder(api, "application", boxFolder, metadata);
            createSubFolder(api, "upload", boxFolder, metadata);
            createSubFolder(api, "confidential", boxFolder, metadata);

            setupResponse(exchange, "201", folderCreationSuccessResponse, FolderCreationSuccessResponse.class);
        } catch (BoxAPIException e) {
            String code = Integer.toString(e.getResponseCode());
            String message = "Internal server error";
            switch (e.getResponseCode()) {
                case 400:
                    message = "Some of the parameters are missing or not valid";
                    break;
                case 403:
                    message = "User does not have the required access to perform the action";
                    break;
                case 404:
                    message = "The parent folder could not be found, or the authenticated user does not have access to the parent folder";
                    break;
                case 409:
                    message = "Folder already exists";
                    break;
            }
            setupError(code, message);
        }
    }

    private void createSubFolder(BoxDeveloperEditionAPIConnection api, String subFolderName, BoxFolder boxFolder, Metadata metadata) {
        try {
            BoxFolder.Info childFolderInfo = boxFolder.createFolder(subFolderName);
            String subFolderId = childFolderInfo.getID();
            BoxFolder subFolder = new BoxFolder(api, subFolderId);
            subFolder.createMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope(), metadata);
        } catch (Exception e) {
            logger.error("Subfolder {} of folder {} could not be created", subFolderName, boxFolder.getInfo().getName());
        }
    }

}
