package com.bssys.chameleon.resource;

import java.io.InputStream;

/**
 * Created by volchik on 30.12.14.
 */

public interface ResourceStorage {

    boolean isResourceExists(String bankId, String moduleType, String moduleName, String resourceName);

    InputStream getResource(String bankId, String moduleType, String moduleName, String resourceName) throws EResourceNotFound;

}
