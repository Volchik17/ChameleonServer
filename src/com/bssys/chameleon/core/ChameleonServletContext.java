package com.bssys.chameleon.core;

import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by volchik on 25.12.14.
 */

public class ChameleonServletContext implements ChameleonContext,ServletContextAware {

    private String basePath="";

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        basePath=servletContext.getRealPath("");
    }

}
