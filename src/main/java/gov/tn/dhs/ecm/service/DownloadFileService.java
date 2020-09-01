package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import gov.tn.dhs.ecm.model.ClientError;
import gov.tn.dhs.ecm.model.FileDownloadRequest;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class DownloadFileService extends BaseService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ConnectionHelper connectionHelper;

    public void downloadFile(Exchange exchange) {
        FileDownloadRequest fileDownloadRequest = exchange.getIn().getBody(FileDownloadRequest.class);
        String fileId = fileDownloadRequest.getFileId();
        BoxDeveloperEditionAPIConnection api = null;
        BoxFile file = null;
        BoxFile.Info info = null;
        try {
            api = connectionHelper.getBoxDeveloperEditionAPIConnection();
            file = new BoxFile(api, fileId);
            info = file.getInfo();
        } catch (BoxAPIException e) {
            setupError("400", "File not found");
        }
        try {
            String fileName = info.getName();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            file.download(outputStream);
            final byte[] bytes = outputStream.toByteArray();
            exchange.getIn().setBody(bytes);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
            exchange.getIn().setHeader("Content-Type", "application/octet-stream");
            String fileNameSuggestion = String.format("attachment; filename=\"%s\"", fileName);
            exchange.getIn().setHeader("Content-Disposition", fileNameSuggestion);
        } catch (Exception ex) {
            setupError("500", "Download error");
        }
    }

}
