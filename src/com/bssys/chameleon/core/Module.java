package com.bssys.chameleon.core;

import javax.servlet.ServletContext;

/**
 * Created by volchik on 28.12.14.
 */

public interface Module {

    RequestHandler getRequestHandler(ServletContext servletContext,String subsystemName, String requestName, String requestVersion);

}
