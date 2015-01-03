package com.bssys.chameleon.core;

import javax.servlet.ServletContext;

/**
 * Created by volchik on 03.01.15.
 */
public class DefaultModuleFactory implements ModuleFactory {

    @Override
    public Module getModule(ServletContext servletContext,String bankId, String moduleType, String moduleName) {
        return new DefaultModule(bankId);
    }

}
