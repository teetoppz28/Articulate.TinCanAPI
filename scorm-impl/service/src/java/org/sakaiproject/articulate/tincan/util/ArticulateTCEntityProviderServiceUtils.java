package org.sakaiproject.articulate.tincan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;

public class ArticulateTCEntityProviderServiceUtils implements ArticulateTCConstants {

    private static Log log = LogFactory.getLog(ArticulateTCEntityProviderServiceUtils.class);

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
     * @param str the payload string from the request
     * @return
     */
    public static String getContentDataFromPayload(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        String decodedPayload = decodeString(str);
        ArticulateTCRequestPayload stateData = new ArticulateTCRequestPayload(decodedPayload);

        if (StringUtils.isBlank(stateData.getContent())) {
            // should not get here... there must not be a "content" portion in the payload
            throw new IllegalArgumentException("Request payload does not contain a valid content statement string.");
        }

        return stateData.getContent();
    }

    /**
     * Gets a mapping of payload data
     * 
     * @param str the payload string from the request
     * @return
     */
    public static ArticulateTCRequestPayload getStateDataFromPayload(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        String decodedPayload = decodeString(str);
        ArticulateTCRequestPayload stateData = new ArticulateTCRequestPayload(decodedPayload);

        if (!stateData.isValid()) {
            // should not get here... there must not be a "content" portion in the payload
            throw new IllegalArgumentException("Request payload does not contain a valid activity state string.");
        }

        return stateData;
    }

}
