package com.bssys.chameleon.metadata;

import java.util.ArrayList;

/**
 * Created by volchik on 20.12.14.
 */
public class MetadataCache {

    ArrayList<MetadataCacheItem> items=new ArrayList<MetadataCacheItem>();

    public MetadataCacheItem getItem(String bankId,String moduleType,String moduleName,String structureName,String langId) {
        for (MetadataCacheItem item:items)
            if (bankId.equals(item.bankId) && moduleType.equalsIgnoreCase(moduleType) && moduleName.equalsIgnoreCase(moduleName) && structureName.equalsIgnoreCase(structureName) && langId.equalsIgnoreCase(item.langId)) {
                return item;
            }
        return null;
    }

    public void updateItem(String bankId,String moduleType,String moduleName,String structureName,String langId,String savedHash,byte[] structure) {
        MetadataCacheItem item=getItem(bankId,moduleType,moduleName,structureName,langId);
        if (item==null) {
            item=new MetadataCacheItem();
            item.bankId=bankId;
            item.langId=langId;
            item.moduleName=moduleName;
            item.moduleType=moduleType;
            item.structureName=structureName;
        }
        item.structure=structure;
        item.savedHash=savedHash;
    }

}
