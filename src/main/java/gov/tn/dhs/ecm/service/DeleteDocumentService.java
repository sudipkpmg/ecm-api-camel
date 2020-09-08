package gov.tn.dhs.ecm.service;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import gov.tn.dhs.ecm.model.DocumentDeletionRequest;
import gov.tn.dhs.ecm.model.DocumentDeletionResult;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeleteDocumentService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteDocumentService.class);

    private final ConnectionHelper connectionHelper;

    public DeleteDocumentService(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    public void deleteDocument(Exchange exchange) {

        BoxDeveloperEditionAPIConnection api = null;
        try {
            api = connectionHelper.getBoxDeveloperEditionAPIConnection();
        } catch (Exception e) {
            setupError("500", "Service error");
        }

        DocumentDeletionRequest documentDeletionRequest = exchange.getIn().getBody(DocumentDeletionRequest.class);
        logger.info(documentDeletionRequest.toString());

        String documentId = documentDeletionRequest.getDocumentId();

        try {
            BoxFile file = new BoxFile(api, documentId);
            file.delete();
        } catch (BoxAPIException e) {
            switch (e.getResponseCode()) {
                case 404: {
                    setupError("409", "Document not found");
                }
                default: {
                    setupError("500", "Document deletion error");
                }
            }
        }

        DocumentDeletionResult documentDeletionResult = new DocumentDeletionResult();
        documentDeletionResult.setMessage("document successfully deleted");
        setupResponse(exchange, "200", documentDeletionResult, DocumentDeletionResult.class);
    }

}


