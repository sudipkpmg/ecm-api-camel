package gov.tn.dhs.ecm.service;

import gov.tn.dhs.ecm.util.ConnectionHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateMetadataService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMetadataService.class);

    public UpdateMetadataService(ConnectionHelper connectionHelper) {
        super(connectionHelper);
    }

    public void process(Exchange exchange) {
        logger.info("update metadata service called ...");
    }

}
