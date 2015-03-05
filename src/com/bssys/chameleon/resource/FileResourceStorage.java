package com.bssys.chameleon.resource;

import com.bssys.chameleon.core.ChameleonContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by volchik on 30.12.14.
 */

@Service
public class FileResourceStorage implements ResourceStorage {

    @Autowired
    private ChameleonContext chameleonContext;

    private Path getFileName(String bankId, String moduleType, String moduleName, String resourceName) {
        String mBankId=bankId==null || bankId.length()==0?"standart":bankId;
        if (moduleName==null || moduleName.length()==0)
            return Paths.get(chameleonContext.getBasePath(), "banks", mBankId, moduleType, resourceName);
        else
            return Paths.get(chameleonContext.getBasePath(), "banks", mBankId, moduleType, moduleName, resourceName);
    }

    @Override
    public boolean isResourceExists(String bankId, String moduleType, String moduleName, String resourceName) {
        File f=getFileName(bankId,moduleType,moduleName,resourceName).toFile();
        return f.exists() && f.isFile();
    }

    @Override
    public InputStream getResource(String bankId, String moduleType, String moduleName, String resourceName) throws EResourceNotFound {
        File f=getFileName(bankId,moduleType,moduleName,resourceName).toFile();
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new EResourceNotFound("Error loading resource: "+f.getAbsolutePath(),e);
        }
    }

    public void setChameleonContext(ChameleonContext chameleonContext) {
        this.chameleonContext = chameleonContext;
    }

}
