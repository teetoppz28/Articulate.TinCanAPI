package org.sakaiproject.scorm.service.tincanapi.impl.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TinCanAPIServiceUtils {

    private static Log log = LogFactory.getLog(TinCanAPIServiceUtils.class);

    private static final String DEFAULT_ENCODING = "UTF-8";

    public static String decodeString(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        try {
            return URLDecoder.decode(str, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error unencoding the string.", e);
        }

        return str;
    }

}
