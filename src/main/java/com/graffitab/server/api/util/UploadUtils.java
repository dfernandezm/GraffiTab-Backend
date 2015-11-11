package com.graffitab.server.api.util;

import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Component
public class UploadUtils {
	
    @Resource
    private CommonsMultipartResolver commonsMultipartResolver;

    @Resource
    private HttpServletRequest httpServletRequest;

    /**
     * Returns the first multipart file for the current HttpServletRequest.
     */
    public MultipartFile getFirstMultipartFileForCurrentRequest() {
        MultipartFile multipartFile = null;
  
        if (commonsMultipartResolver.isMultipart(httpServletRequest)) {
            MultipartHttpServletRequest multipartHttpServletRequest = commonsMultipartResolver.resolveMultipart(httpServletRequest);
            Iterator<String> fileNamesIterator = multipartHttpServletRequest.getFileNames();
            if (fileNamesIterator.hasNext()) {
                String firstFileName = fileNamesIterator.next();
                multipartFile = multipartHttpServletRequest.getFile(firstFileName);
            }
        }
        
        return multipartFile;
    }
	
}
