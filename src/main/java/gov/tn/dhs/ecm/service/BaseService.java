package gov.tn.dhs.ecm.service;

import gov.tn.dhs.ecm.exception.ServiceErrorException;
import gov.tn.dhs.ecm.model.ClientError;
import gov.tn.dhs.ecm.util.JsonUtil;
import org.apache.camel.Exchange;

public class BaseService {

    protected void setupResponse(Exchange exchange, String code, Object response, Class clazz) {
        exchange.getIn().setBody(response, clazz);
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, code);
        exchange.getIn().setHeader("Content-Type", "application/json");
        exchange.getIn().setHeader("Accept", "application/json");
    }

    protected void setupError(String code, String message) {
        ClientError clientError = new ClientError(code, message);
        throw new ServiceErrorException(code, JsonUtil.toJson(clientError));
    }

}
