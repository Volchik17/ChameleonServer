package com.bssys.chameleon.servlet;

import com.bssys.chameleon.config.BankConfiguration;
import com.bssys.chameleon.config.ServerConfigurationManager;
import com.bssys.chameleon.core.RequestHandler;
import com.bssys.chameleon.core.Subsystem;
import com.bssys.chameleon.core.SubsystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by volchik on 29.12.14.
 */
@Controller
public class InfoRequestController {

    @Autowired
    private ServerConfigurationManager serverConfigurationManager;

    private boolean isInvalidParam(String param) {
        return param==null || !param.matches("(\\w|\\.)*");
    }

    @RequestMapping(value = "/inforequest",method = RequestMethod.POST)
    public void request(ServletRequest request, HttpServletResponse response) throws Exception {
        String bankId=request.getParameter("bankId");
        String moduleType=request.getParameter("moduleType");
        String moduleName=request.getParameter("moduleName");
        String requestName=request.getParameter("request");
        String langId=request.getParameter("langId");
        String version=request.getParameter("version");
        version=version==null?"1":version;
        BankConfiguration bank=serverConfigurationManager.getServerConfiguration().getBanks().get(bankId);
        if (bank==null)
            throw new Exception("Bank with id \""+bankId+"\" not found");
        SubsystemFactory factory=(SubsystemFactory)Class.forName(bank.getSubsystemFactoryClassName()).newInstance();
        Subsystem subsystem=factory.getSubsystem(moduleType, moduleName);
        RequestHandler handler=subsystem.getRequestHandler(moduleName, requestName, version);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document=builder.parse(new InputSource(request.getInputStream()));
        XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer=xmlFactory.createXMLStreamWriter(response.getWriter());
        writer.writeStartDocument("UTF8","1.0");
        handler.processRequest(moduleName,version,document,writer);
        response.getWriter().flush();
        response.getWriter().close();
    }

    public void setServerConfigurationManager(ServerConfigurationManager serverConfigurationManager) {
        this.serverConfigurationManager = serverConfigurationManager;
    }
}
