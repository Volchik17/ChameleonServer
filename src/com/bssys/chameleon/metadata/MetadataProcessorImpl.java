package com.bssys.chameleon.metadata;

/**
 * Created by volchik on 20.12.14.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Properties;

@Component
public class MetadataProcessorImpl implements MetadataProcessor{

    static final String defaultBankId="standart";

    static final String inheritanceURI="http://www.bssys.com.MBSClient.XMLInheritance.html";

    @Autowired
    private MetadataStorage metadataStorage;


    private void localizeElement(Element element,Properties strings) {
        NodeList list=element.getChildNodes();
        for (int i=0;i<list.getLength();i++) {
            switch (list.item(i).getNodeType()) {
                case Node.ELEMENT_NODE:
                    localizeElement((Element)list.item(i),strings);
                    break;
                case Node.TEXT_NODE:
                    Text txt=(Text)list.item(i);
                    String value=txt.getData();
                    if (value.matches("^%\\w+%$")) {
                        value=value.substring(1,value.length()-1);
                        value=strings.getProperty(value);
                        if (value!=null)
                            txt.setData(value);
                    }
                    break;
            }
        }
        NamedNodeMap attrs=element.getAttributes();
        for (int i=0;i<attrs.getLength();i++) {
            Attr att=(Attr)attrs.item(i);
            String value=att.getValue();
            if (value.matches("^%\\w+%$")) {
                value=value.substring(1,value.length()-1);
                value=strings.getProperty(value);
                if (value!=null)
                    att.setValue(value);
            }
        }
    }

    private boolean isAttributeYes(Element element, String attributeName, boolean defaultValue) {
        if (!element.hasAttribute(attributeName))
            return defaultValue;
        String value=element.getAttribute(attributeName);
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equals("1");
    }

    private boolean isAttributeYesNS(Element element, String namespaceURI,String attributeName, boolean defaultValue) {
        if (!element.hasAttributeNS(namespaceURI, attributeName))
            return defaultValue;
        String value=element.getAttributeNS(namespaceURI, attributeName);
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equals("1");
    }

    private String inheritanceXPathExpression(Element element, String attributeName) {
        if (element.getNamespaceURI()!=null)
            return "//*[local-name()='"+element.getLocalName()+"' and "+
                    "namespace-uri()='"+element.getNamespaceURI()+"' and "+
                    "@"+attributeName+"='"+element.getAttribute(attributeName)+"']";
        else
            return "//*[local-name()='"+element.getLocalName()+"' and "+
                    "@"+attributeName+"='"+element.getAttribute(attributeName)+"']";
    }

    private String inheritanceXPathExpression(Element element, String attributeName,String attributeValue) {
        if (element.getNamespaceURI()!=null)
            return "//*[local-name()='"+element.getLocalName()+"' and "+
                    "namespace-uri()='"+element.getNamespaceURI()+"' and "+
                    "@"+attributeName+"='"+attributeValue+"']";
        else
            return "//*[local-name()='"+element.getLocalName()+"' and "+
                    "@"+attributeName+"='"+attributeValue+"']";
    }

    private void joinElements(Element baseParentNode,Element childParentNode) throws XPathExpressionException {
        // Добавляем и заменяем атрибуты
        for (int i=0;i<childParentNode.getAttributes().getLength();i++)
            if (childParentNode.getAttributes().item(i).getNamespaceURI()==null || !childParentNode.getAttributes().item(i).getNamespaceURI().equals(inheritanceURI))
                baseParentNode.setAttribute(childParentNode.getAttributes().item(i).getNodeName(),childParentNode.getAttributes().item(i).getNodeValue());
        // Удаляем атрибуты
        if (childParentNode.hasAttributeNS(inheritanceURI,"deleted_attributes")) {
            String deletedAttributes=childParentNode.getAttributeNS(inheritanceURI, "deleted_attributes");
            for (String attributeName:deletedAttributes.split(","))
                baseParentNode.removeAttribute(attributeName);
        }
        // обрабатываем подэлементы
        if (childParentNode.hasAttributeNS(inheritanceURI, "list")) {
            // Режим списка
            String idAttributeName=childParentNode.getAttributeNS(inheritanceURI, "list");
            Element addingPosition=null;
            NodeList list=childParentNode.getChildNodes();
            for (int i=0;i<list.getLength();i++) {
                if (list.item(i).getNodeType()==Node.ELEMENT_NODE) {
                    Element childSubElement=(Element)list.item(i);
                    String position=childSubElement.getAttributeNS(inheritanceURI,"position");
                    if (position==null)
                        position="";
                    XPath xPath= XPathFactory.newInstance().newXPath();
                    NodeList nodeList = (NodeList) xPath.compile(inheritanceXPathExpression(childSubElement,idAttributeName)).evaluate(baseParentNode, XPathConstants.NODESET);
                    if (isAttributeYesNS(childSubElement,inheritanceURI,"deleted",false)) {
                        for (int j=0;j<nodeList.getLength();j++)
                            baseParentNode.removeChild(nodeList.item(j));
                    } else if (nodeList.getLength()==0) {
                        Element baseSubElement=baseParentNode.getOwnerDocument().createElement(childSubElement.getTagName());
                        if (position.equals("")) {
                            if (addingPosition==null) {
                                baseParentNode.appendChild(baseSubElement);
                            } else {
                                baseParentNode.insertBefore(baseSubElement,addingPosition.getNextSibling());
                            }
                        } else if (position.equals("first")) {
                            baseParentNode.insertBefore(baseSubElement,baseParentNode.getFirstChild());
                        } else if (position.equals("last")) {
                            baseParentNode.appendChild(baseSubElement);
                        } else if (position.startsWith("after:")) {
                            String afterId=position.substring("after:".length());
                            xPath= XPathFactory.newInstance().newXPath();
                            nodeList = (NodeList) xPath.compile(inheritanceXPathExpression(childSubElement,idAttributeName,afterId)).evaluate(baseParentNode, XPathConstants.NODESET);
                            if (nodeList.getLength()==0)
                                baseParentNode.appendChild(baseSubElement);
                            else {
                                Node node=nodeList.item(0).getNextSibling();
                                if (node==null)
                                    baseParentNode.appendChild(baseSubElement);
                                else
                                    baseParentNode.insertBefore(baseSubElement,node);
                            }
                        } else if (position.startsWith("before:")) {
                            String beforeId=position.substring("before:".length());
                            xPath= XPathFactory.newInstance().newXPath();
                            nodeList = (NodeList) xPath.compile(inheritanceXPathExpression(childSubElement,idAttributeName,beforeId)).evaluate(baseParentNode, XPathConstants.NODESET);
                            if (nodeList.getLength()==0)
                                baseParentNode.appendChild(baseSubElement);
                            else {
                                baseParentNode.insertBefore(baseSubElement,nodeList.item(0));
                            }
                        }
                        addingPosition=baseSubElement;
                        joinElements(baseSubElement,childSubElement);
                    } else {
                        for(int j=0;j<nodeList.getLength();j++) {
                            Element baseSubElement=(Element)nodeList.item(j);
                            if (!position.equals("")) {
                                baseParentNode.removeChild(baseSubElement);
                                if (position.equals("first"))
                                    baseParentNode.insertBefore(baseSubElement,baseParentNode.getFirstChild());
                                else if (position.equals("last"))
                                    baseParentNode.appendChild(baseSubElement);
                                else if (position.startsWith("after:")) {
                                    String afterId=position.substring("after:".length());
                                    xPath= XPathFactory.newInstance().newXPath();
                                    nodeList = (NodeList) xPath.compile(inheritanceXPathExpression(childSubElement,idAttributeName,afterId)).evaluate(baseParentNode, XPathConstants.NODESET);
                                    if (nodeList.getLength()==0)
                                        baseParentNode.appendChild(baseSubElement);
                                    else {
                                        Node node=nodeList.item(0).getNextSibling();
                                        if (node==null)
                                            baseParentNode.appendChild(baseSubElement);
                                        else
                                            baseParentNode.insertBefore(baseSubElement,node);
                                    }
                                } else if (position.startsWith("before:")) {
                                    String beforeId=position.substring("before:".length());
                                    xPath= XPathFactory.newInstance().newXPath();
                                    nodeList = (NodeList) xPath.compile(inheritanceXPathExpression(childSubElement,idAttributeName,beforeId)).evaluate(baseParentNode, XPathConstants.NODESET);
                                  if (nodeList.getLength()==0)
                                        baseParentNode.appendChild(baseSubElement);
                                    else {
                                        baseParentNode.insertBefore(baseSubElement,nodeList.item(0));
                                    }
                                }
                            }
                            joinElements(baseSubElement,childSubElement);
                        }

                    }

                }
            }
        } else {
            // Режим простой замены
            NodeList list=childParentNode.getChildNodes();
            for (int i=0;i<list.getLength();i++) {
                if (list.item(i).getNodeType()==Node.ELEMENT_NODE) {
                        Element childSubElement=(Element)list.item(i);
                        if (isAttributeYesNS(childSubElement, inheritanceURI, "deleted", false)) {
                            NodeList subElements=baseParentNode.getElementsByTagNameNS(childSubElement.getNamespaceURI(),childSubElement.getLocalName());
                            for (int j=0;j<subElements.getLength();j++)
                                baseParentNode.removeChild(subElements.item(j));
                        } else {
                            NodeList subElements=baseParentNode.getElementsByTagNameNS(childSubElement.getNamespaceURI(),childSubElement.getLocalName());
                            if (subElements.getLength()==0) {
                                Element baseSubElement=baseParentNode.getOwnerDocument().createElementNS(childSubElement.getNamespaceURI(), childSubElement.getLocalName());
                                baseParentNode.appendChild(baseSubElement);
                                joinElements(baseSubElement,childSubElement);
                            } else
                                for (int j=0;j<subElements.getLength();j++)
                                    if (subElements.item(j).getNodeType()==Node.ELEMENT_NODE)
                                        joinElements((Element)subElements.item(j),childSubElement);
                        }

                }
            }
        }
    }

    private void joinStructure(Document document,Document childDocument) throws EInvalidMetadata {
        Element baseParentNode=document.getDocumentElement();
        Element childParentNode=childDocument.getDocumentElement();
        try {
            joinElements(baseParentNode,childParentNode);
        } catch (XPathExpressionException e) {
            throw new EInvalidMetadata("Error joining meta structures.",e);
        }
    }

    private void UnifyStructure(Element element) {
        // Удаляем inheritance атрибуты
        for (int i=0;i<element.getAttributes().getLength();i++)
            if (element.getAttributes().item(i).getNamespaceURI()!=null &&element.getAttributes().item(i).getNamespaceURI().equals(inheritanceURI))
                element.removeAttributeNS(inheritanceURI,element.getAttributes().item(i).getLocalName());
        // Удаляем текстовые ноды и осуществляем рекурсию
        NodeList list=element.getChildNodes();
        for (int i=list.getLength()-1;i>=0;i--) {
            if (list.item(i).getNodeType()==Node.ELEMENT_NODE)
                UnifyStructure((Element)list.item(i));
            else if (list.item(i).getNodeType()==Node.TEXT_NODE)
                element.removeChild(list.item(i));
        }
    }

    private Document getLocalizedDocument(String bankId,String moduleType,String moduleName,String structureName,String langId) throws EInvalidMetadata {

        Document document=metadataStorage.getStructure(bankId,moduleType,moduleName,structureName);
        if((langId==null) || (langId.length()==0))
            return document;
        if (!metadataStorage.isLocalizedResourcesExist(bankId,moduleType,moduleName,structureName,langId))
            return  document;
        Properties strings=metadataStorage.getLocalizedResources(bankId,moduleType,moduleName,structureName,langId);
        NodeList list=document.getDocumentElement().getChildNodes();
        for (int i=0;i<list.getLength();i++) {
            if (list.item(i).getNodeType()==Node.ELEMENT_NODE)
                localizeElement((Element)list.item(i),strings);
        }
        return document;
    }

    public byte[] combineXML(String bankId,String moduleType,String moduleName,String structureName,String langId) throws EInvalidMetadata {

        // making hierarchy
        Document document;
        ArrayList<Document> hierarchy=new ArrayList<Document>();
        do  {
            if (metadataStorage.isStructureExists(bankId,moduleType,moduleName,structureName)) {
                document=getLocalizedDocument(bankId,moduleType,moduleName,structureName,langId);
                hierarchy.add(0,document);
                if (isAttributeYesNS(document.getDocumentElement(),inheritanceURI,"patch",false)) {
                    if (metadataStorage.isStructureExists(defaultBankId,moduleType,moduleName,structureName)) {
                        document=getLocalizedDocument(defaultBankId, moduleType, moduleName, structureName, langId);
                        hierarchy.add(0,document);
                    }
                }
            } else {
                if (!metadataStorage.isStructureExists(defaultBankId,moduleType,moduleName,structureName))
                    throw new EInvalidMetadata(String.format("Meta structure does not exists. BankId: %s. ModuleType: %s. ModuleName: %s. StructureName: %s.",defaultBankId,moduleType,moduleName,structureName));
                document=getLocalizedDocument(defaultBankId, moduleType, moduleName, structureName, langId);
                hierarchy.add(0,document);
            }
            if (document.getDocumentElement().hasAttributeNS(inheritanceURI,"parent"))
                moduleName=document.getDocumentElement().getAttributeNS(inheritanceURI,"parent");
            else
                moduleName="";
        } while (moduleName.length()>0);
        // joining structures in hierarchy from bottom to top
        Document structure=hierarchy.get(0);
        for (int i=1;i<hierarchy.size();i++)
            joinStructure(structure,hierarchy.get(i));
        // removing text nodes and inheritance attributes
        structure.getDocumentElement().removeAttribute("xmlns:inheritence");
        UnifyStructure(structure.getDocumentElement());
        // serializing xml
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            transformer.transform(new DOMSource(structure), new StreamResult(stream));
            return stream.toByteArray();
        } catch (TransformerException e) {
            throw new EInvalidMetadata(String.format("Error serializing meta structure xml. BankId: %s. ModuleType: %s. ModuleName: %s. StructureName: %s.",defaultBankId,moduleType,moduleName,structureName),e);
        }
    }

    public void setMetadataStorage(MetadataStorage metadataStorage) {
        this.metadataStorage = metadataStorage;
    }

}
