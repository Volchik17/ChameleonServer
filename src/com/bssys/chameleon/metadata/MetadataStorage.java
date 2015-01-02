package com.bssys.chameleon.metadata;

import org.w3c.dom.Document;

import java.util.Properties;

/**
 * Created by volchik on 26.12.14.
 */

public interface MetadataStorage {

    Properties getLocalizedResources(String bankId,String moduleType,String moduleName,String structureName, String langId);

    boolean isLocalizedResourcesExist(String bankId,String moduleType,String moduleName,String structureName, String langId);

    Document getStructure(String bankId,String moduleType,String moduleName,String structureName) throws EInvalidMetadata;

    boolean isStructureExists(String bankId,String moduleType,String moduleName,String structureName);

}
