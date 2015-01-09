package com.bssys.chameleon.resource;

import com.bssys.chameleon.core.ChameleonContext;
import com.bssys.chameleon.core.HashAlgorithm;
import com.bssys.chameleon.core.Log;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by volchik on 30.12.14.
 */

public class ResourceManager {

    static final String defaultBankId="standart";

    private static ResourceManager mInstance=new ResourceManager();
    private Map<String,String> fileHashes=new HashMap<String,String>();

    @Autowired
    private ChameleonContext chameleonContext;

    @Autowired
    private Log log;

    @Autowired
    private ResourceStorage storage;

    @Resource(name = "SHA256Hash")
    private HashAlgorithm hashAlgorithm;

    public static synchronized ResourceManager instance() {
        return mInstance;
    }

    private InputStream getResourceForBank(String bankId,String moduleType,String moduleName,String resourceName,String savedHash) throws Exception {
        String resId=bankId+"|"+moduleType+"|"+moduleName+"|"+resourceName;
        String serverHash;
        synchronized (this) {
            if (fileHashes.containsKey(resId))
                serverHash=fileHashes.get(resId);
            else
                serverHash="";
        }
        if (savedHash.length()>0 && serverHash.equals(savedHash))
            return null;
        if (!storage.isResourceExists(bankId,moduleType,moduleName,resourceName))
            throw new EResourceNotFound("Resource does not exists: ");
        InputStream result=storage.getResource(bankId,moduleType,moduleName,resourceName);
        String newHash=hashAlgorithm.calculate(result);
        if (!newHash.equals(serverHash))
            synchronized (this) {
                fileHashes.put(resId,newHash);
            }
        if (newHash.equals(savedHash))
            return null;
        else
            // Нельзя просто вернуть result, так как он уже вычитан после вычисления hash-а. Нужно получить заново
            return storage.getResource(bankId,moduleType,moduleName,resourceName);
    }

    public InputStream getResource(String bankId,String moduleType,String moduleName,String resourceName,String savedHash) throws Exception {
        try {
            return getResourceForBank(bankId,moduleType,moduleName,resourceName,savedHash);
        } catch (EResourceNotFound e) {
            // if resource not found for requested bank searching it in default
            if (!bankId.equalsIgnoreCase(defaultBankId)) {
                try {
                    return getResourceForBank(defaultBankId,moduleType,moduleName,resourceName,savedHash);
                } catch (EResourceNotFound e1) {
                    // magic for throwing exception of loading resource for requested bank, not default
                    throw e;
                }
            } else
                throw e;
        }
    }

    public void setChameleonContext(ChameleonContext chameleonContext) {
        this.chameleonContext = chameleonContext;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void setStorage(ResourceStorage storage) {
        this.storage = storage;
    }

    public void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

}
