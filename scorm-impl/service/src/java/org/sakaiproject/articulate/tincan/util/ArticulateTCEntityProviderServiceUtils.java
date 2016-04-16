package org.sakaiproject.articulate.tincan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

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
     * @param str
     * @return
     */
    public static String getContentDataFromPayload(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        String decodedPayload = decodeString(str);

        String[] split = StringUtils.split(decodedPayload, "&");
        for (String s : split) {
            if (StringUtils.startsWith(s, STATEMENT_DATA_KEY_CONTENT)) {
                return StringUtils.removeStart(s, STATEMENT_DATA_KEY_CONTENT + "=");
            }
        }

        // should not get here... there must not be a "content" portion in the payload
        throw new IllegalArgumentException("Request payload does not contain a valid content statement string.");
    }

    /**
     * Gets a mapping of payload data
     * 
     * @param str
     * @return
     */
    public static Map<String, String> getStateDataFromPayload(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        Map<String, String> stateData = new HashMap<String, String>();

        String decodedPayload = decodeString(str);

        String[] split = StringUtils.split(decodedPayload, "&");
        for (String s : split) {
            if (StringUtils.startsWith(s, STATE_DATA_KEY_ACTIVITY_ID)) {
                stateData.put(STATE_DATA_KEY_ACTIVITY_ID, StringUtils.removeStart(s, STATE_DATA_KEY_ACTIVITY_ID + "="));
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_AGENT)) {
                stateData.put(STATE_DATA_KEY_AGENT, StringUtils.removeStart(s, STATE_DATA_KEY_AGENT + "="));
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_CONTENT)) {
                stateData.put(STATE_DATA_KEY_CONTENT, StringUtils.removeStart(s, STATE_DATA_KEY_CONTENT + "="));
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_STATE_ID)) {
                stateData.put(STATE_DATA_KEY_STATE_ID, StringUtils.removeStart(s, STATE_DATA_KEY_STATE_ID + "="));
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_SITE_ID)) {
                stateData.put(STATE_DATA_KEY_SITE_ID, StringUtils.removeStart(s, STATE_DATA_KEY_SITE_ID + "="));
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_USER_ID)) {
                stateData.put(STATE_DATA_KEY_USER_ID, StringUtils.removeStart(s, STATE_DATA_KEY_USER_ID + "="));
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_PACKAGE_ID)) {
                stateData.put(STATE_DATA_KEY_PACKAGE_ID, StringUtils.removeStart(s, STATE_DATA_KEY_PACKAGE_ID + "="));
                continue;
            }
        }

        // should not get here... there must not be a "content" portion in the payload
        if (stateData.isEmpty()) {
            throw new IllegalArgumentException("Request payload does not contain a valid activity state string.");
        }

        return stateData;
    }

}
