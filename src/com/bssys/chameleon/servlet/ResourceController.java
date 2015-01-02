package com.bssys.chameleon.servlet;

import com.bssys.chameleon.resource.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by volchik on 29.12.14.
 */

@Controller
public class ResourceController {

    @Autowired
    private ResourceManager resourceManager;

    private boolean isInvalidParam(String param) {
        return param==null || !param.matches("(\\w|\\.)*");
    }

    private static final int BUFFER_SIZE = 2048;
    private static final int EOF_MARK = -1;

    private static int writeFromInputToOutput(InputStream source, OutputStream dest) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = EOF_MARK;
        int count = 0;
        try {
            while ((bytesRead = source.read(buffer)) != EOF_MARK) {
                dest.write(buffer, 0, bytesRead);
                count += bytesRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    @RequestMapping(value = "/resource")
    public void request(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String bankId=request.getParameter("bankId");
        if (bankId==null)
            bankId="";
        String moduleType=request.getParameter("moduleType");
        String moduleName=request.getParameter("moduleName");
        if (moduleName==null)
            moduleName="";
        String resourceName=request.getParameter("resourceName");
        String savedHash=request.getParameter("savedHash");
        if (savedHash==null)
            savedHash="";
        if (isInvalidParam(bankId) || isInvalidParam(moduleType) || isInvalidParam(moduleName) || isInvalidParam(resourceName) || isInvalidParam(savedHash))
            throw new IOException("Invalid characters in resource request parameters.");
        InputStream stream=resourceManager.getResource(bankId, moduleType, moduleName, resourceName, savedHash);
        if (stream==null)
        {
            response.setStatus(304);
        }
        else
        {
            writeFromInputToOutput(stream,response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
}
