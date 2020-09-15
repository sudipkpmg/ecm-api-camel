package gov.tn.dhs.ecm.route;

import com.fasterxml.jackson.core.JsonParseException;
import gov.tn.dhs.ecm.exception.ServiceErrorException;
import gov.tn.dhs.ecm.model.*;
import gov.tn.dhs.ecm.service.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
class EcmApiRoutes extends RouteBuilder {

    public final CreateFolderService createFolderService;

    public final DownloadFileService downloadFileService;

    public final UploadFileService uploadFileService;

    public final SearchService searchService;

    public final DeleteDocumentService deleteDocumentService;

    public final DocumentViewService viewDocumentService;

    public final ApplyMetadataService applyMetadataService;

    public final UpdateMetadataService updateMetadataService;

    @Value("${server.port}")
    private String serverPort;

    @Value("${runstatus}")
    private String runStatus;

    public EcmApiRoutes(
            CreateFolderService createFolderService,
            DownloadFileService downloadFileService,
            UploadFileService uploadFileService,
            SearchService searchService,
            DeleteDocumentService deleteDocumentService,
            DocumentViewService viewDocumentService,
            ApplyMetadataService applyMetadataService,
            UpdateMetadataService updateMetadataService
    ) {
        this.createFolderService = createFolderService;
        this.downloadFileService = downloadFileService;
        this.uploadFileService = uploadFileService;
        this.searchService = searchService;
        this.deleteDocumentService = deleteDocumentService;
        this.viewDocumentService = viewDocumentService;
        this.applyMetadataService = applyMetadataService;
        this.updateMetadataService = updateMetadataService;
    }

    @Override
    public void configure() {

        onException(JsonParseException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(constant("{}"))
        ;

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("${exception.message}"))
        ;

        onException(ServiceErrorException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exception.code}"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("${exception.message}"))
        ;

        restConfiguration()
                .enableCORS(true)
                .apiProperty("cors", "true") // cross-site
                .component("servlet")
                .port(serverPort)
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        defineStatusPath();

        defineCreateFolderPath();

        defineDownloadFilePath();

        defineUploadFilePath();

        defineSearchPath();

        defineDeleteDocumentPath();

        defineViewDocumentPath();

        defineApplyMetadataPath();

        defineUpdateMetadataPath();

    }

    private void defineStatusPath() {
        SimpleMessage simpleMessage = new SimpleMessage(runStatus);
        rest()
                .get("/")
                .to("direct:runningStatus")
        ;
        from("direct:runningStatus")
                .log("Status request sent")
                .log("runstatus property value is " + runStatus)
                .process(exchange -> {
                    exchange.getIn().setBody(simpleMessage, SimpleMessage.class);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setHeader("Content-Type", constant("application/json"))
                .endRest()
        ;
    }

    private void defineCreateFolderPath() {
        rest()
                .post("/create_folder")
                .type(CitizenMetadata.class)
                .outType(FolderCreationSuccessResponse.class)
                .to("direct:createFolderService")
        ;
        from("direct:createFolderService")
                .bean(createFolderService)
                .endRest()
        ;
    }

    private void defineDownloadFilePath() {
        rest()
                .post("/download_file")
                .type(FileDownloadRequest.class)
                .outType(byte[].class)
                .to("direct:downloadFileService")
        ;
        from("direct:downloadFileService")
                .bean(downloadFileService)
                .endRest()
                ;
    }

    private void defineUploadFilePath() {
        rest()
                .bindingMode(RestBindingMode.off)
                .post("/upload_file")
                .outType(String.class)
                .to("direct:uploadFile")
        ;
        from("direct:uploadFile")
                .unmarshal()
                .mimeMultipart()
                .bean(uploadFileService)
                .endRest()
        ;
    }

    private void defineSearchPath() {
        rest()
                .post("/search")
                .type(SearchRequest.class)
                .outType(SearchResult.class)
                .to("direct:searchService")
        ;
        from("direct:searchService")
                .bean(searchService)
                .endRest()
        ;
    }

    private void defineDeleteDocumentPath() {
        rest()
                .post("/delete_document")
                .type(DocumentDeletionRequest.class)
                .outType(DocumentDeletionResult.class)
                .to("direct:deleteDocumentService")
        ;
        from("direct:deleteDocumentService")
                .bean(deleteDocumentService)
                .endRest()
        ;
    }

    private void defineViewDocumentPath() {
        rest()
                .post("/view_document")
                .type(DocumentViewRequest.class)
                .outType(DocumentViewResult.class)
                .to("direct:viewDocumentService")
        ;
        from("direct:viewDocumentService")
                .bean(viewDocumentService)
                .endRest()
        ;
    }

    private void defineApplyMetadataPath() {
        rest()
                .post("/apply_metadata")
                .type(MetadataAdditionRequest.class)
                .outType(MetadataAdditionResponse.class)
                .to("direct:applyMetadataService")
        ;
        from("direct:applyMetadataService")
                .bean(applyMetadataService)
                .endRest()
        ;
    }

    private void defineUpdateMetadataPath() {
        rest()
                .post("/update_metadata")
                .type(MetadataUpdationRequest.class)
                .outType(MetadataUpdationResponse.class)
                .to("direct:updateMetadataService")
        ;
        from("direct:updateMetadataService")
                .bean(updateMetadataService)
                .endRest()
        ;
    }

}
