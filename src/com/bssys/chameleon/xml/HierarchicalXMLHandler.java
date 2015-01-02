package com.bssys.chameleon.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 * Created by volchik on 14.12.14.
 */
class HierarchicalXMLHandler extends DefaultHandler {

    private class StackItem {
        IStructureParser parser;
        int level;

        StackItem(IStructureParser parser,int level) {
            this.parser=parser;
            this.level=level;
        }
    }

    Stack<StackItem> parserStack;
    Stack<StringBuffer> charsStack;

    int mLevel;

    public HierarchicalXMLHandler (IStructureParser selfParser) {
        mLevel=0;
        parserStack=new Stack<StackItem>();
        parserStack.push(new StackItem(selfParser,mLevel));
        charsStack=new Stack<StringBuffer>();
    }

    @Override
    public  void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (mLevel==0) {
            parserStack.peek().parser.startParsing(uri,localName,qName,attributes);
            mLevel++;
            charsStack.push(new StringBuffer());
        } else {
            mLevel++;
            charsStack.push(new StringBuffer());
            IStructureParser newParser = parserStack.peek().parser.startElement(uri, localName, qName, attributes);
            if (newParser != null) {
                parserStack.push(new StackItem(newParser, mLevel));
                newParser.startParsing(uri, localName, qName, attributes);
            }
        }
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException
    {
        mLevel--;
        if (parserStack.empty()) return;
        if (parserStack.peek().level==mLevel+1) {
            parserStack.pop().parser.endParsing(uri, localName, qName,charsStack.peek().toString());
        } else {
            parserStack.peek().parser.endElement(uri, localName, qName, charsStack.peek().toString());
        }
        charsStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!charsStack.empty()) {
            charsStack.peek().append(ch, start, length);
        }
    }

}
