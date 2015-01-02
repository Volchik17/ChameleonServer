package com.bssys.chameleon.gates;

import com.bssys.chameleon.xml.IStructureParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Created by volchik on 15.12.14.
 */
public class DBO3GateAddress extends GateAddress {

    public static String DBO3_GATE_TYPE="dbo3";

    private String url;

    public String getGateType() {
        return DBO3_GATE_TYPE;
    }

    public IStructureParser getParser() {
        return new IStructureParser() {
            @Override
            public void startParsing(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                url=attributes.getValue("url");
            }

            @Override
            public IStructureParser startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                return null;
            }

            @Override
            public void endElement(String uri, String localName, String qName, String charData) throws SAXException {

            }

            @Override
            public void endParsing(String uri, String localName, String qName, String charData) throws SAXException {

            }
        };
    }

    public String getUrl() {
        return url;
    }
}
