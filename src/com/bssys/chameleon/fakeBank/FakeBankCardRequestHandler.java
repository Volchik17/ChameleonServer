package com.bssys.chameleon.fakeBank;

import com.bssys.chameleon.core.RequestHandler;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by volchik on 03.01.15.
 */
public class FakeBankCardRequestHandler implements RequestHandler {

    @Override
    public void processRequest(ServletContext servletContext, String moduleName, String requestVersion, Document request, XMLStreamWriter writer) throws Exception {
        writer.writeStartElement("bankCard");
        writer.writeStartElement("fields");
        writer.writeStartElement("field");
        writer.writeAttribute("name", "bankName");
        writer.writeAttribute("value","Банк \"СИР\"");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
    }

}
