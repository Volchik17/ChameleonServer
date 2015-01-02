package com.bssys.chameleon.metadata;

import com.bssys.chameleon.core.ChameleonContext;
import com.bssys.chameleon.core.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.*;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by volchik on 26.12.14.
 */

public class FileMetadataStorage implements MetadataStorage {

    static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA =
            "http://www.w3.org/2001/XMLSchema";

    @Autowired
    private ChameleonContext chameleonContext;

    @Autowired
    private Log log;

    private Path getLocalizedResourcesFileName(String bankId,String moduleType,String moduleName,String structureName, String langId) {
        if (moduleName==null || moduleName.length()==0)
            return Paths.get(chameleonContext.getBasePath(),"banks",bankId,moduleType,structureName+".strings."+langId+".properties");
        else
            return Paths.get(chameleonContext.getBasePath(),"banks",bankId,moduleType,moduleName,structureName+".strings."+langId+".properties");
    }

    @Override
    public Properties getLocalizedResources(String bankId, String moduleType, String moduleName, String structureName, String langId) {
        File file=getLocalizedResourcesFileName(bankId,moduleType,moduleName,structureName, langId).toFile();
        Properties strings=new Properties();
        if (file.exists() && file.isFile())
            try {
                strings.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            } catch (FileNotFoundException e) {log.Warning("Metadata","Localized resources do not exists %s",file.getAbsolutePath());} catch (IOException e) {log.Error("Metadata",e,"Error loading localized resources: %s",file.getAbsolutePath());}
        return strings;
    }

    @Override
    public boolean isLocalizedResourcesExist(String bankId,String moduleType,String moduleName,String structureName, String langId) {
        File file=getLocalizedResourcesFileName(bankId,moduleType,moduleName,structureName, langId).toFile();
        return file.exists() && file.isFile();
    }

    private Path getDocumentFileName(String bankId, String moduleType, String moduleName, String structureName) {
        if (moduleName==null || moduleName.length()==0)
            return Paths.get(chameleonContext.getBasePath(),"banks", bankId, moduleType, structureName + ".xml");
        else
            return Paths.get(chameleonContext.getBasePath(),"banks", bankId, moduleType, moduleName, structureName + ".xml");
    }

    private ErrorHandler getErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        };
    }

    private String getSchemasLocation(String moduleType) {
        return Paths.get(chameleonContext.getBasePath(),"schemas",moduleType).toString();
    }

    @Override
    public Document getStructure(String bankId, String moduleType, String moduleName, String structureName) throws EInvalidMetadata{
        File file=getDocumentFileName(bankId, moduleType, moduleName, structureName).toFile();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        Document document;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(getErrorHandler());
            final String mModuleType=moduleType;
            EntityResolver resolver=new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    String fileName=new File(systemId).getName();
                    String commonSchemas=getSchemasLocation("common");
                    String moduleSchemas=getSchemasLocation(mModuleType);
                    File f=Paths.get(moduleSchemas,fileName).toFile();
                    if (f.exists() && f.isFile())
                        return new InputSource(new FileInputStream(f));
                    f=Paths.get(commonSchemas,fileName).toFile();
                    if (f.exists() && f.isFile())
                        return new InputSource(new FileInputStream(f));
                    return null;
                }
            };
            builder.setEntityResolver(resolver);
            document=builder.parse(file);
        } catch (Exception e) {
            throw new EInvalidMetadata("Error loading meta structure: "+file.getAbsolutePath(),e);
        }
        return document;
    }

    @Override
    public boolean isStructureExists(String bankId, String moduleType, String moduleName, String structureName) {
        File file=getDocumentFileName(bankId, moduleType, moduleName, structureName).toFile();
        return file.exists() && file.isFile();
    }

    public void setChameleonContext(ChameleonContext chameleonContext) {
        this.chameleonContext = chameleonContext;
    }

    public ChameleonContext getChameleonContext() {
        return chameleonContext;
    }

    public void setLog(Log log) {
        this.log = log;
    }
}
