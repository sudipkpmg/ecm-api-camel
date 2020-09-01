package gov.tn.dhs.ecm.service;

import com.box.sdk.*;
import com.eclipsesource.json.JsonObject;
import gov.tn.dhs.ecm.config.AppProperties;
import gov.tn.dhs.ecm.exception.ServiceErrorException;
import gov.tn.dhs.ecm.model.*;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateFolderService extends BaseService {

    @Autowired
    ConnectionHelper connectionHelper;

    @Autowired
    AppProperties appProperties;

    public void createFolder(Exchange exchange) throws ServiceErrorException {
        try {
            FolderCreationRequest folderCreationRequest = exchange.getIn().getBody(FolderCreationRequest.class);
            logger.info(folderCreationRequest.toString());

            BoxDeveloperEditionAPIConnection api = connectionHelper.getBoxDeveloperEditionAPIConnection();

            String parentFolderId = appProperties.getParentFolderID();
            BoxFolder parentFolder = new BoxFolder(api, parentFolderId);

            String folderName = folderCreationRequest.getCitizenMetadata().getMpiId();
            BoxFolder.Info childFolderInfo = parentFolder.createFolder(folderName);

            String folderID = childFolderInfo.getID();

            FolderCreationSuccessResponse folderCreationSuccessResponse = new FolderCreationSuccessResponse();
            folderCreationSuccessResponse.setId(folderID);

            BoxFolder boxFolder = new BoxFolder(api, folderID);
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("FirstName", folderCreationRequest.getCitizenMetadata().getFirstName());
            jsonObject.add("LastName", folderCreationRequest.getCitizenMetadata().getLastName());
            jsonObject.add("last4ofssn", folderCreationRequest.getCitizenMetadata().getSsn4());
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
        BoxFolder.Info childFolderInfo = boxFolder.createFolder(subFolderName);
        String subFolderId = childFolderInfo.getID();
        BoxFolder subFolder = new BoxFolder(api, subFolderId);
        subFolder.createMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope(), metadata);
    }

}
