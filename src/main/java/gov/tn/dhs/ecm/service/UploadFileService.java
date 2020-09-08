package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import gov.tn.dhs.ecm.config.AppProperties;
import gov.tn.dhs.ecm.model.UploadFileResponse;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import gov.tn.dhs.ecm.util.JsonUtil;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.io.InputStream;

@Service
public class UploadFileService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(UploadFileService.class);

    private final ConnectionHelper connectionHelper;

    private final AppProperties appProperties;

    public UploadFileService(ConnectionHelper connectionHelper, AppProperties appProperties) {
        this.connectionHelper = connectionHelper;
        this.appProperties = appProperties;
    }

    public void uploadFile(Exchange exchange) throws Exception {
        String boxFolderId = exchange.getIn().getBody(String.class);
        DataHandler[] attachments = exchange.getIn().getAttachments().values().toArray(new DataHandler[0]);
        DataHandler dh = attachments[0];
        InputStream fileStream = dh.getInputStream();
        String fileName = dh.getName();
        uploadToBox(exchange, fileStream, fileName, boxFolderId);
    }

    private void uploadToBox(Exchange exchange, InputStream inputStream, String fileName, String boxFolderId) {
        BoxDeveloperEditionAPIConnection api = connectionHelper.getBoxDeveloperEditionAPIConnection();
        BoxFolder parentFolder = null;
        try {
            parentFolder = new BoxFolder(api, boxFolderId);
            BoxFolder.Info info = parentFolder.getInfo();
            logger.info("Parent Folder with ID {} and name {} found", boxFolderId, info.getName());
        } catch (BoxAPIException e) {
            setupError("400", "Folder not found");
        }
        String fileId = "No File Created";
        BoxFile.Info newFileInfo = null;
        try {
            newFileInfo = parentFolder.uploadFile(inputStream, fileName);
        } catch (BoxAPIException e) {
            setupError("409", "File with the same name already exists");
        }
        fileId = newFileInfo.getID();
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setStatus("File upload completed");
        uploadFileResponse.setFileId(fileId);
        setupResponse(exchange, "200", JsonUtil.toJson(uploadFileResponse), String.class);
    }

}
