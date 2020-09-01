package gov.tn.dhs.ecm.route;

import com.fasterxml.jackson.core.JsonParseException;
import gov.tn.dhs.ecm.exception.ServiceErrorException;
import gov.tn.dhs.ecm.model.*;
import gov.tn.dhs.ecm.service.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class EcmApiRoutes extends RouteBuilder {

    @Autowired
    public CreateFolderService createFolderService;

    @Autowired
    public GetFileService getFileService;

    @Autowired
    public DownloadFileService downloadFileService;

    @Autowired
    public UploadFileService uploadFileService;

    @Autowired
    public SearchService searchService;

    @Value("${server.port}")
    String serverPort;

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

        defineGetFilePath();

        defineDownloadFilePath();

        defineUploadFilePath();

        defineSearchPath();

    }

    private void defineSearchPath() {
        rest()
                .post("/search")
                .type(Query.class)
                .to("direct:searchService")
                .outType(SearchResult.class)
        ;
        from("direct:searchService")
                .bean(searchService, "search")
                .outputType(SearchResult.class)
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

    private void defineGetFilePath() {
        rest()
                .get("/get_file/{fileId}")
                .outType(byte[].class)
                .to("direct:getFileService")
        ;
        from("direct:getFileService")
                .bean(getFileService, "getFile")
                .endRest()
        ;
    }

    private void defineStatusPath() {
        rest()
                .get("/")
                .to("direct:runningStatus")
        ;
        from("direct:runningStatus")
                .transform().simple("TNDHS ECM API is running")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }

    private void defineCreateFolderPath() {
        rest()
                .post("/createFolder")
                .type(FolderCreationRequest.class)
                .to("direct:createFolderService")
                .outType(FolderCreationSuccessResponse.class)
        ;
        from("direct:createFolderService")
                .bean(createFolderService, "createFolder")
                .endRest()
        ;
    }

    private void defineUploadFilePath() {
        rest()
                .bindingMode(RestBindingMode.off)
                .post("/upload_file")
                .to("direct:uploadFile")
                .outType(UploadFileResponse.class)
        ;
        from("direct:uploadFile")
                .unmarshal()
                .mimeMultipart()
                .bean(uploadFileService, "uploadFile")
                .endRest()
        ;

    }

}
