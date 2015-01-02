package com.bssys.chameleon.core;

import org.w3c.dom.Document;

import javax.xml.stream.XMLStreamWriter;

/**
 * Created by volchik on 30.12.14.
 */
public interface RequestHandler {

    void processRequest(String moduleName,String requestVersion,Document request, XMLStreamWriter writer);

}
