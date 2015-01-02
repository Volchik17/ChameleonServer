package com.bssys.chameleon.core;

/**
 * Created by volchik on 28.12.14.
 */

public interface Subsystem {

    RequestHandler getRequestHandler(String subsystemName, String requestVersion, String requestName);

}
