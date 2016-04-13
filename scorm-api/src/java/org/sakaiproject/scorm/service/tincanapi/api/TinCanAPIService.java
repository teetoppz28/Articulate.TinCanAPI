package org.sakaiproject.scorm.service.tincanapi.api;

import javax.servlet.http.HttpServletRequest;

public interface TinCanAPIService {

    String getRequestPayload(HttpServletRequest request);

    String getContentFromPayload(String str);

    Object getObjectFromJSON(String jsonStr);

}
