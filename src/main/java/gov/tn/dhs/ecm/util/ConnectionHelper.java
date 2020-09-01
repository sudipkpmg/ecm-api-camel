package gov.tn.dhs.ecm.util;

import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import gov.tn.dhs.ecm.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectionHelper {

    @Autowired
    private AppProperties appProperties;

    public BoxDeveloperEditionAPIConnection getBoxDeveloperEditionAPIConnection() {
        String clientId = appProperties.getClientID();
        String clientSecret = appProperties.getClientSecret();
        String enterpriseID = appProperties.getEnterpriseID();
        String publicKeyID = appProperties.getPublicKeyID();
        String privateKey = appProperties.getPrivateKey();
        String passphrase = appProperties.getPassphrase();
        BoxConfig boxConfig = new BoxConfig(
                clientId,
                clientSecret,
                enterpriseID,
                publicKeyID,
                privateKey,
                passphrase
        );
        BoxDeveloperEditionAPIConnection api = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
//        api.asUser(appProperties.getDownloadOneUserID());
        return api;
    }

}
