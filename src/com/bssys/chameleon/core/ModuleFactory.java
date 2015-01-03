package com.bssys.chameleon.core;

import javax.servlet.ServletContext;

/**
 * Created by volchik on 28.12.14.
 */

public interface ModuleFactory {

    public Module getModule(ServletContext servletContext,String bankId, String moduleType, String moduleName);

}
