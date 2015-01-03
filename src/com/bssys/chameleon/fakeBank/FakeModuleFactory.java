package com.bssys.chameleon.fakeBank;

import com.bssys.chameleon.core.Module;
import com.bssys.chameleon.core.ModuleFactory;

import javax.servlet.ServletContext;

/**
 * Created by volchik on 03.01.15.
 */
public class FakeModuleFactory implements ModuleFactory {
    @Override
    public Module getModule(ServletContext servletContext, String bankId, String moduleType, String moduleName) {
        if (moduleType.equalsIgnoreCase("bankCard"))
            return new FakeBankCardModule();
        else
            throw new RuntimeException("Unknown module:"+moduleType);
    }
}
