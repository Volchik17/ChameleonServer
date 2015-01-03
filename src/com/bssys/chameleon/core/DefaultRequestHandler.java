package com.bssys.chameleon.core;

import com.bssys.chameleon.config.ServerConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by volchik on 03.01.15.
 */

public class DefaultRequestHandler implements RequestHandler {

    private DefaultModule module;

    public DefaultRequestHandler(final DefaultModule module) {
        super();
        this.module=module;
    }

    @Override
    public void processRequest(ServletContext servletContext,String moduleName, String requestVersion, Document request, XMLStreamWriter writer) throws Exception {
        WebApplicationContext webContext= WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        ServerConfigurationManager serverConfigurationManager=(ServerConfigurationManager)webContext.getBean("ServerConfigurationManager");
        String gateName=serverConfigurationManager.getServerConfiguration().getBanks().get(module.getBankId()).getDefaultGate();

    }

}
