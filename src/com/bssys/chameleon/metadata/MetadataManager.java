package com.bssys.chameleon.metadata;

import com.bssys.chameleon.core.HashAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by volchik on 20.12.14.
 */
public class MetadataManager {

    private static MetadataManager mInstance=new MetadataManager();

    private MetadataCache cache=new MetadataCache();

    @Autowired
    private MetadataProcessor metadataProcessor;

    @Autowired
    private HashAlgorithm hashAlgorithm;

    public static synchronized MetadataManager instance() {
        return mInstance;
    }

    public byte[] getMetadata(String bankId,String moduleType,String moduleName,String structureName,String langId,String savedHash) throws Exception {
        // ищем в кэше
        MetadataCacheItem item;
        synchronized (this) {
            item=cache.getItem(bankId,moduleType,moduleName,structureName,langId);
            if (item!=null)
                if (item.savedHash.equals(savedHash))
                    return null;
                else
                    return item.structure;
        }
        // строим на основе xml файлов метаданных
        byte[] structure=metadataProcessor.combineXML(bankId,moduleType,moduleName,structureName,langId);
        String newHash=hashAlgorithm.calculate(structure);
        // заносим в кэш
        synchronized (this) {
            cache.updateItem(bankId, moduleType, moduleName, structureName, langId, newHash, structure);
        }
        if (newHash.equals(savedHash))
            return null;
        else
            return structure;
    }

    public void setMetadataProcessor(MetadataProcessor metadataProcessor) {
        this.metadataProcessor = metadataProcessor;
    }

    public void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
}
