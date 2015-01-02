package com.bssys.chameleon.metadata;

/**
 * Created by volchik on 26.12.14.
 */

public interface MetadataProcessor {

    public byte[] combineXML(String bankId,String moduleType,String moduleName,String structureName,String langId) throws Exception ;

}
