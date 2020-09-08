package gov.tn.dhs.ecm.route;

import com.fasterxml.jackson.core.JsonParseException;
import gov.tn.dhs.ecm.exception.ServiceErrorException;
import gov.tn.dhs.ecm.model.*;
import gov.tn.dhs.ecm.service.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class EcmApiRoutes extends RouteBuilder {

    public final CreateFolderService createFolderService;

    public final DownloadFileService downloadFileService;

    public final UploadFileService uploadFileService;

    public final SearchService searchService;

    public final DeleteDocumentService deleteDocumentService;

    public final DocumentViewService viewDocumentService;

    @Value("${server.port}")
    String serverPort;

    public EcmApiRoutes(
            CreateFolderService createFolderService,
            DownloadFileService downloadFileService,
            UploadFileService uploadFileService,
            SearchService searchService,
            DeleteDocumentService deleteDocumentService,
            DocumentViewService viewDocumentService
    ) {
        this.createFolderService = createFolderService;
        this.downloadFileService = downloadFileService;
        this.uploadFileService = uploadFileService;
        this.searchService = searchService;
        this.deleteDocumentService = deleteDocumentService;
        this.viewDocumentService = viewDocumentService;
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
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "ECM API")
                .apiProperty("api.version", "v1")
                .apiProperty("cors", "true") // cross-site
                .apiContextRouteId("doc-api")
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

    }

    private void defineStatusPath() {
        rest()
                .get("/")
                .to("direct:runningStatus")
        ;
        from("direct:runningStatus")
                .transform().simple("TNDHS ECM API is running")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
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
                .bean(createFolderService, "createFolder")
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
                .bean(downloadFileService, "downloadFile")
                .endRest()
                ;
    }

    private void defineUploadFilePath() {
        rest()
                .bindingMode(RestBindingMode.off)
                .post("/upload_file")
                .outType(UploadFileResponse.class)
                .to("direct:uploadFile")
        ;
        from("direct:uploadFile")
                .unmarshal()
                .mimeMultipart()
                .bean(uploadFileService, "uploadFile")
                .endRest()
        ;
    }

    private void defineSearchPath() {
        rest()
                .post("/search")
                .type(Query.class)
                .outType(SearchResult.class)
                .to("direct:searchService")
        ;
        from("direct:searchService")
                .bean(searchService, "search")
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
                .bean(deleteDocumentService, "deleteDocument")
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
                .bean(viewDocumentService, "viewDocument")
                .endRest()
        ;
    }

}
