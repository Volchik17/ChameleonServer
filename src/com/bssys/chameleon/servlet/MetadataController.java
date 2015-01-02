package com.bssys.chameleon.servlet;

/**
 * Created by volchik on 25.12.14.
 */
import com.bssys.chameleon.metadata.MetadataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MetadataController {

    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    @Autowired
    private MetadataManager metadataManager;

    private boolean isInvalidParam(String param) {
        return param==null || !param.matches("(\\w|\\.)*");
    }

    @RequestMapping(value = "/metadata")
    public void metadata(ServletRequest request, HttpServletResponse response) throws Exception {
        String bankId=request.getParameter("bankId");
        if (bankId==null)
            bankId="";
        String moduleType=request.getParameter("moduleType");
        String moduleName=request.getParameter("moduleName");
        if (moduleName==null)
            moduleName="";
        String structureName=request.getParameter("structureName");
        String langId=request.getParameter("langId");
        if (langId==null)
            langId="";
        String savedHash=request.getParameter("savedHash");
        if (savedHash==null)
            savedHash="";
        if (isInvalidParam(bankId) || isInvalidParam(moduleType) || isInvalidParam(moduleName)|| isInvalidParam(structureName) || isInvalidParam(langId))
            throw new IOException("Invalid characters in metadata request parameters.");
        byte[] structure=metadataManager.getMetadata(bankId,moduleType,moduleName,structureName,langId,savedHash);
        if (structure==null)
        {
            response.setStatus(304);
        }
        else
        {
            response.getOutputStream().write(structure);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }
    }

}