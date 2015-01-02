package com.bssys.chameleon.config;

import com.bssys.chameleon.core.ChameleonContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by volchik on 14.12.14.
 */
public class ServerConfigurationManager {

    @Autowired
    ChameleonContext context;

    static ServerConfiguration currentConfiguration=null;

    public synchronized ServerConfiguration getServerConfiguration() throws ParserConfigurationException, SAXException, IOException {
        if (currentConfiguration==null)
        {
            currentConfiguration=new ServerConfiguration(context.getBasePath()+"/config/config.xml");
        }
        return currentConfiguration;
    }

    public void setContext(ChameleonContext context) {
        this.context=context;
    }
}
