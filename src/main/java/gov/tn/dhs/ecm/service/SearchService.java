package gov.tn.dhs.ecm.service;

import com.box.sdk.*;
import gov.tn.dhs.ecm.config.AppProperties;
import gov.tn.dhs.ecm.model.CitizenMetadata;
import gov.tn.dhs.ecm.model.FileInfo;
import gov.tn.dhs.ecm.model.Query;
import gov.tn.dhs.ecm.model.SearchResult;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final AppProperties appProperties;

    public SearchService(ConnectionHelper connectionHelper, AppProperties appProperties) {
        super(connectionHelper);
        this.appProperties = appProperties;
    }

    public void process(Exchange exchange) {
        BoxDeveloperEditionAPIConnection api = getBoxApiConnection();

        Query query = exchange.getIn().getBody(Query.class);
        long offset = query.getOffset();
        long limit = query.getLimit();

        switch (query.getSearchType().toLowerCase()) {
            case "folder": {
                String folderId = query.getFolderId();
                try {
                    BoxFolder folder = new BoxFolder(api, folderId);
                    Metadata folderMetadata = folder.getMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope());
                    logger.info(folderMetadata.toString());
                    List<FileInfo> files = new ArrayList<>();
                    PartialCollection<BoxItem.Info> items = folder.getChildrenRange(offset, limit);
                    for (BoxItem.Info itemInfo : items) {
                        FileInfo fileInfo = getItemInfo(itemInfo, folderMetadata);
                        files.add(fileInfo);
                    }
                    long allItemCount = items.fullSize();
                    boolean complete = (allItemCount > (offset+limit));
                    prepareSearchResult(exchange, files, complete);
                } catch (BoxAPIException e) {
                    int responseCode = e.getResponseCode();
                    switch (responseCode) {
                        case 404: {
                            setupError("404", "Folder not found");
                        }
                        default: {
                            setupError("500", "Search error");
                        }
                    }
                }
                break;
            }
            case "file": {
                String folderId = query.getFolderId();
                String fileName = query.getFileName();
                BoxFolder folder = new BoxFolder(api, folderId);
                Metadata folderMetadata = folder.getMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope());
                limit++;
                long position = offset;
                long count = 0;
                List<FileInfo> files = new ArrayList<>();
                for (BoxItem.Info info : folder) {
                    if (info instanceof BoxFile.Info) {
                        String itemName = info.getName();
                        if (fileName.equals(itemName)) {
                            position++;
                            if (position >= offset) {
                                FileInfo fileInfo = getItemInfo(info, folderMetadata);
                                files.add(fileInfo);
                                count++;
                                if (count == limit) {
                                    break;
                                }
                            }
                        }
                    }
                }
                boolean complete = (count < limit);
                prepareSearchResult(exchange, files, complete);
                break;
            }
        }

    }

    private void prepareSearchResult(Exchange exchange, List<FileInfo> files, boolean complete) {
        SearchResult searchResult = new SearchResult();
        searchResult.setFileData(files);
        searchResult.setComplete(Boolean.toString(complete));
        setupResponse(exchange, "200", searchResult, SearchResult.class);
    }

    private FileInfo getItemInfo(BoxItem.Info itemInfo, Metadata folderMetadata) {
        FileInfo fileInfo = new FileInfo();
        String fileId = itemInfo.getID();
        String name = itemInfo.getName();
        String itemType = itemInfo.getType();
        fileInfo.setFileId(fileId);
        fileInfo.setFileName(name);
        fileInfo.setItemType(itemType);
        CitizenMetadata citizenMetadata = getCitizenMetadata(folderMetadata);
        fileInfo.setCitizenMetadata(citizenMetadata);
        return fileInfo;
    }

    private CitizenMetadata getCitizenMetadata(Metadata folderMetadata) {
        CitizenMetadata citizenMetadata = new CitizenMetadata();
        citizenMetadata.setFirstName(getMetadataStringField(folderMetadata, "/FirstName"));
        citizenMetadata.setLastName(getMetadataStringField(folderMetadata, "/LastName"));
        citizenMetadata.setSsn4(getMetadataStringField(folderMetadata, "/last4ofssn"));
        citizenMetadata.setLogonUserId(getMetadataStringField(folderMetadata, "/logonuserid"));
        citizenMetadata.setMpiId(getMetadataStringField(folderMetadata, "/mpiid"));
        citizenMetadata.setSysId(getMetadataStringField(folderMetadata, "/sysid"));
        citizenMetadata.setDob(getMetadataDateField(folderMetadata,"/dob1"));
        return citizenMetadata;
    }

    private String getMetadataStringField(Metadata metadata, String fieldPath) {
        String fieldValue = null;
        try {
            fieldValue = metadata.getString(fieldPath);
        } catch (Exception e)
        {}
        return fieldValue;
    }

    private LocalDate getMetadataDateField(Metadata metadata, String fieldPath) {
        LocalDate fieldValue = null;
        try {
            String fieldValueAsString = metadata.getString(fieldPath);
            String dateValueAsString = fieldValueAsString.substring(0, fieldValueAsString.indexOf('T'));
            fieldValue = LocalDate.parse(dateValueAsString);
        } catch (Exception e)
        {}
        return fieldValue;
    }

}
