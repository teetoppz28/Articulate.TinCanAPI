package org.sakaiproject.scorm.service.tincanapi.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.LearningResourceStoreService;
import org.sakaiproject.scorm.service.tincanapi.api.TinCanAPIEntityProviderService;
import org.sakaiproject.scorm.service.tincanapi.impl.util.TinCanAPIEntityProviderServiceUtils;

public class TinCanAPIEntityProviderServiceImpl implements TinCanAPIEntityProviderService {

    private Log log = LogFactory.getLog(TinCanAPIEntityProviderServiceImpl.class);

    private LearningResourceStoreService learningResourceStoreService;

    public void init() {
        learningResourceStoreService = (LearningResourceStoreService) ComponentManager.get("org.sakaiproject.event.api.LearningResourceStoreService");
    }

    @Override
    public String processContentPayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String payload = TinCanAPIEntityProviderServiceUtils.getRequestPayload(request);
        String contentStr = TinCanAPIEntityProviderServiceUtils.getContentFromPayload(payload);

        return contentStr;
    }

    @Override
    public void sendStatementToLRS(String statementJson) {
        if (StringUtils.isBlank(statementJson)) {
            throw new IllegalArgumentException("Statement JSON cannot be empty.");
        }

        try {
            LearningResourceStoreService.LRS_Statement statement = new LearningResourceStoreService.LRS_Statement(statementJson);
            learningResourceStoreService.registerStatement(statement, null);
        } catch (Exception e) {
            log.error("Error sending statement to LRS :: Statement JSON: \n" + statementJson, e);
        }
    }

}
