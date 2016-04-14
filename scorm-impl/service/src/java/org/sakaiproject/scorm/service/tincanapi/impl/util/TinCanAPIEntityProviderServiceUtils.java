package org.sakaiproject.scorm.service.tincanapi.impl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIConstants;

public class TinCanAPIEntityProviderServiceUtils implements TinCanAPIConstants {

    private static Log log = LogFactory.getLog(TinCanAPIEntityProviderServiceUtils.class);

    /**
     * Decodes the URL-encoded string
     * 
     * @param str
     * @return
     */
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

    /**
     * Gets the payload from the {@link HttpServletRequest} object
     * 
     * @param request
     * @return
     */
    public static String getRequestPayload(HttpServletRequest request) {
        String content = "";

        try {
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            content = sb.toString();
        } catch (IOException e) {
            log.error("Error retrieving the request payload.", e);
        }

        return content;
    }

    /**
     * Gets the JSON string from the content variable in the payload
     * 
     * @param str
     * @return
     */
    public static String getContentFromPayload(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        String decodedPayload = TinCanAPIEntityProviderServiceUtils.decodeString(str);

        String[] split = StringUtils.split(decodedPayload, "&");
        for (String s : split) {
            if (StringUtils.startsWith(s, PAYLOAD_CONTENT_VARIABLE)) {
                return StringUtils.removeStart(s, PAYLOAD_CONTENT_VARIABLE);
            }
        }

        // should not get here... there must not be a "content" portion in the payload
        throw new IllegalArgumentException("Request payload does not contain a valid content statement string.");
    }

}
