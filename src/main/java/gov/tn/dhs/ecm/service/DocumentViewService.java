package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import gov.tn.dhs.ecm.model.DocumentViewRequest;
import gov.tn.dhs.ecm.model.DocumentViewResult;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
@Slf4j
public class DocumentViewService extends BaseService {

    private final ConnectionHelper connectionHelper;

    public DocumentViewService(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    public void viewDocument(Exchange exchange) {

        BoxDeveloperEditionAPIConnection api = null;
        try {
            api = connectionHelper.getBoxDeveloperEditionAPIConnection();
        } catch (Exception e) {
            setupError("500", "Service error");
        }

        DocumentViewRequest documentViewRequest = exchange.getIn().getBody(DocumentViewRequest.class);
        String documentId = documentViewRequest.getDocumentId();

        try {
            BoxFile file = new BoxFile(api, documentId);
            URL previewUrl = file.getPreviewLink();
            DocumentViewResult documentViewResult = new DocumentViewResult();
            documentViewResult.setPreviewUrl(previewUrl.toString());
            setupResponse(exchange, "200", documentViewResult, DocumentViewResult.class);
        } catch (BoxAPIException e) {
            switch (e.getResponseCode()) {
                case 404: {
                    setupError("409", "Document not found");
                }
                default: {
                    setupError("500", "Document view error");
                }
            }
        }

    }

}



