package com.bssys.chameleon.servlet;

/**
 * Created by volchik on 25.12.14.
 */
import com.bssys.chameleon.config.BankConfiguration;
import com.bssys.chameleon.config.ServerConfiguration;
import com.bssys.chameleon.config.ServerConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

@Controller
public class PingController {

    @Autowired
    private ServerConfigurationManager serverConfigurationManager;

    @RequestMapping(value = "/ping")
    public void ping(HttpServletRequest request, ServletResponse response) throws Exception {
        ServerConfiguration config=serverConfigurationManager.getServerConfiguration();
        XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer=xmlFactory.createXMLStreamWriter(response.getWriter());
        writer.writeStartDocument("UTF8","1.0");
        writer.writeStartElement("Banks");
        for (BankConfiguration bank:config.getBanks().values()) {
            writer.writeStartElement("Bank");
            writer.writeAttribute("id",bank.getId());
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.flush();
        writer.close();
    }

    public void setServerConfigurationManager(ServerConfigurationManager serverConfigurationManager) {
        this.serverConfigurationManager = serverConfigurationManager;
    }
}
