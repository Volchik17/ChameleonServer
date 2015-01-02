package com.bssys.chameleon.config;

import com.bssys.chameleon.xml.HierarchicalXMLParser;
import com.bssys.chameleon.xml.IStructureParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by volchik on 14.12.14.
 */
public class ServerConfiguration {

    public HashMap<String, BankConfiguration> getBanks() {
        return banks;
    }

    private HashMap<String,BankConfiguration> banks=new HashMap<String, BankConfiguration>();

    public ServerConfiguration(String fileName) throws IOException, ParserConfigurationException, SAXException {
        super();
        HierarchicalXMLParser parser=new HierarchicalXMLParser();
        parser.parseStructure(new FileInputStream(fileName),getParser());
    }

    private IStructureParser getParser() {
        return new IStructureParser() {
            @Override
            public void startParsing(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (!qName.equals("ServerConfig"))
                    throw new SAXException("Root element of configuration file is \""+localName+"\" instead of \"ServerConfig\"");
            }

            @Override
            public IStructureParser startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equals("Banks"))
                    return getBanksParser();
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

    private IStructureParser getBanksParser() {
        return new IStructureParser() {
            @Override
            public void startParsing(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            }

            @Override
            public IStructureParser startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equals("Bank")) {
                    BankConfiguration bank=new BankConfiguration();
                    String id=attributes.getValue("id");
                    banks.put(id,bank);
                    return bank.getParser();
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
