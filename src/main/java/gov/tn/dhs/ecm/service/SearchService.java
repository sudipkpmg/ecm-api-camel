package gov.tn.dhs.ecm.service;

import com.box.sdk.*;
import gov.tn.dhs.ecm.config.AppProperties;
import gov.tn.dhs.ecm.model.*;
import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService extends BaseService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ConnectionHelper connectionHelper;

    @Autowired
    private AppProperties appProperties;

    public void search(Exchange exchange) {
        Query query = exchange.getIn().getBody(Query.class);
        BoxDeveloperEditionAPIConnection api = connectionHelper.getBoxDeveloperEditionAPIConnection();
        switch (query.getSearchType().toLowerCase()) {
            case "folder": {
                String folderId = query.getFolderId();
                try {
                    BoxFolder folder = new BoxFolder(api, folderId);
                    Metadata folderMetadata = folder.getMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope());
                    logger.info(folderMetadata.toString());
                    String folderMetadata_template_id = folderMetadata.getID();
                    logger.info("ID of Folder Metadata Template = {}", folderMetadata_template_id);
                    List<FileInfo> files = new ArrayList<>();
                    for (BoxItem.Info itemInfo : folder.getChildren()) {
                        FileInfo fileInfo = getItemInfo(itemInfo, folderMetadata);
                        files.add(fileInfo);
                    }
                    prepareSearchResult(exchange, files);
                } catch (BoxAPIException e) {
                    setupError("500", "Search error");
                }
                break;
            }
            case "file": {
                String folderId = query.getFolderId();
                String fileName = query.getFileName();
                BoxFolder folder = new BoxFolder(api, folderId);
                Metadata folderMetadata = folder.getMetadata(appProperties.getCitizenFolderMetadataTemplateName(), appProperties.getCitizenFolderMetadataTemplateScope());
                long offsetValue = 0;
                long limitValue = 20;
                BoxSearch boxSearch = new BoxSearch(api);
                BoxSearchParameters searchParams = new BoxSearchParameters();
                searchParams.setQuery(fileName);
                List<String> ancestorFolderIds = new ArrayList<String>();
                ancestorFolderIds.add(query.getFolderId());
                searchParams.setAncestorFolderIds(ancestorFolderIds);
                searchParams.setType("file");
                PartialCollection<BoxItem.Info> searchResults = boxSearch.searchRange(offsetValue, limitValue, searchParams);
                List<FileInfo> files = new ArrayList<>();
                for (BoxItem.Info info : searchResults) {
                    if (fileName.equals(info.getName())) {
                        FileInfo fileInfo = getItemInfo(info, folderMetadata);
                        files.add(fileInfo);
                    }
                }
                prepareSearchResult(exchange, files);
                break;
            }
        }
    }

    private void prepareSearchResult(Exchange exchange, List<FileInfo> files) {
        SearchResult searchResult = new SearchResult();
        searchResult.setFileData(files);
        searchResult.setComplete("true");
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
        citizenMetadata.setFirstName(folderMetadata.getString("/FirstName"));
        citizenMetadata.setLastName(folderMetadata.getString("/LastName"));
        citizenMetadata.setSsn4(folderMetadata.getString("/last4ofssn"));
        return citizenMetadata;
    }

}
