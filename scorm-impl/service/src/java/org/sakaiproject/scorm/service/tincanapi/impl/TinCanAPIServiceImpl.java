package org.sakaiproject.scorm.service.tincanapi.impl;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIConstants;
import org.sakaiproject.scorm.service.tincanapi.api.TinCanAPIService;
import org.sakaiproject.scorm.service.tincanapi.impl.util.TinCanAPIJsonUtils;
import org.sakaiproject.scorm.service.tincanapi.impl.util.TinCanAPIServiceUtils;

public class TinCanAPIServiceImpl implements TinCanAPIService {

    @Override
    public String getRequestPayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

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
            e.printStackTrace();
        }

        return content;
    }

    @Override
    public String getContentFromPayload(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        String decodedPayload = TinCanAPIServiceUtils.decodeString(str);

        String[] split = StringUtils.split(decodedPayload, "&");
        for (String s : split) {
            if (StringUtils.startsWith(s, TinCanAPIConstants.PAYLOAD_CONTENT_VARIABLE)) {
                return StringUtils.removeStart(s, TinCanAPIConstants.PAYLOAD_CONTENT_VARIABLE);
            }
        }

        // should not be here... there must not be a "content" portion in the payload
        throw new IllegalArgumentException("Request payload does not contain a valid content statement string.");
    }

    @Override
    public Object getObjectFromJSON(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            throw new IllegalArgumentException("JSON string cannot be blank");
        }

        return TinCanAPIJsonUtils.parseFromJsonObject(jsonStr);
    }

}
