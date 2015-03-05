package com.bssys.chameleon.servlet;

import com.bssys.chameleon.config.BankConfiguration;
import com.bssys.chameleon.config.ServerConfigurationManager;
import com.bssys.chameleon.core.Module;
import com.bssys.chameleon.core.ModuleFactory;
import com.bssys.chameleon.core.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.ServletContext;
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

    @Autowired
    private ServletContext servletContext;

    private boolean isInvalidParam(String param) {
        return param==null || !param.matches("(\\w|\\.)*");
    }

    @RequestMapping(value = "/inforequest"/*,method = RequestMethod.POST*/)
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
        ModuleFactory factory=(ModuleFactory)Class.forName(bank.getModuleFactoryClassName()).newInstance();
        Module module =factory.getModule(servletContext,bankId,moduleType, moduleName);
        RequestHandler handler= module.getRequestHandler(servletContext,moduleName, requestName, version);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document;
        if (request.getInputStream().isFinished())
            document=builder.newDocument();
        else
            document=builder.parse(new InputSource(request.getInputStream()));
        XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer=xmlFactory.createXMLStreamWriter(response.getOutputStream(),"UTF-8");
        writer.writeStartDocument("UTF-8","1.0");
        handler.processRequest(servletContext,moduleName,version,document,writer);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    public void setServerConfigurationManager(ServerConfigurationManager serverConfigurationManager) {
        this.serverConfigurationManager = serverConfigurationManager;
    }

    public void setServletContext(final ServletContext servletContext) {
        this.servletContext=servletContext;
    }
}
