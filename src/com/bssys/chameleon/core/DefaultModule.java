package com.bssys.chameleon.core;

import javax.servlet.ServletContext;

/**
 * Created by volchik on 03.01.15.
 */

public class DefaultModule implements Module {

    private String bankId;

    public DefaultModule(final String bankId) {
        super();
        this.bankId=bankId;
    }

    @Override
    public RequestHandler getRequestHandler(ServletContext servletContext,String subsystemName, String requestName, String requestVersion) {
        return null;
    }

    public String getBankId() {
        return bankId;
    }

}
