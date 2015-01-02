package com.bssys.chameleon.xml;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by volchik on 14.12.14.
 */

public class HierarchicalXMLParser {

    public static void parseStructure(InputStream is, IStructureParser parser) throws SAXException,IOException,ParserConfigurationException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        if (parser==null)
            return;
        saxParser.parse(is,new HierarchicalXMLHandler(parser));
    }

}