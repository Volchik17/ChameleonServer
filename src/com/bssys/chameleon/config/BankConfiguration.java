package com.bssys.chameleon.config;

import com.bssys.chameleon.gates.GateAddress;
import com.bssys.chameleon.gates.GateFactory;
import com.bssys.chameleon.xml.IStructureParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashMap;

/**
 * Created by volchik on 14.12.14.
 */
public class BankConfiguration {

    public String getId() {
        return id;
    }

    public String getSubsystemFactoryClassName() {
        return subsystemFactoryClassName;
    }

    public String getDefaultGate() {
        return defaultGate;
    }

    public HashMap<String, GateAddress> getGates() {
        return gates;
    }

    private String id;
    private String subsystemFactoryClassName;
    private String defaultGate;
    private HashMap<String,GateAddress> gates=new HashMap<String, GateAddress>();

    IStructureParser getParser() {
        return new IStructureParser() {
            @Override
            public void startParsing(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                id=attributes.getValue("id");
                subsystemFactoryClassName=attributes.getValue("subsystemFactoryClassName");
                defaultGate=attributes.getValue("defaultGate");
            }

            @Override
            public IStructureParser startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equals("Gates")) {
                    return getGatesParser();
                }
                else
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

    private IStructureParser getGatesParser() {
        return new IStructureParser() {
            @Override
            public void startParsing(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            }

            @Override
            public IStructureParser startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equals("Gate")) {
                    String gateType=attributes.getValue("type");
                    GateAddress gate= GateFactory.createGateAddress(gateType);
                    String name=attributes.getValue("name");
                    gates.put(name,gate);
                    return gate.getParser();
                }
                else
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
}
