package com.bssys.chameleon.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Created by volchik on 14.12.14.
 */

public interface IStructureParser {

    void startParsing(String uri, String localName, String qName, Attributes attributes) throws SAXException;

    IStructureParser startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;

    void endElement(String uri, String localName, String qName, String charData) throws SAXException;

    void endParsing(String uri, String localName, String qName, String charData) throws SAXException;

}
