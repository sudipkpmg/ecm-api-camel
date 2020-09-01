package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class GetFileService extends BaseService {

    @Autowired
    private ConnectionHelper connectionHelper;

    public void getFile(Exchange exchange) {
        String fileId = exchange.getIn().getHeader("fileId", String.class);
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

