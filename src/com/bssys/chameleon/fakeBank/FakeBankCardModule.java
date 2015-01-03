package com.bssys.chameleon.fakeBank;

import com.bssys.chameleon.core.Module;
import com.bssys.chameleon.core.RequestHandler;

import javax.servlet.ServletContext;

/**
 * Created by volchik on 03.01.15.
 */
public class FakeBankCardModule implements Module {
    @Override
    public RequestHandler getRequestHandler(ServletContext servletContext, String subsystemName, String requestName, String requestVersion) {
        if (requestName.equalsIgnoreCase("bankCard"))
            return new FakeBankCardRequestHandler();
        else
            throw new RuntimeException("Unknown request: "+requestName);
    }

}
