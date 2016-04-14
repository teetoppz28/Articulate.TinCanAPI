package org.sakaiproject.articulate.tincan.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.util.ArticulateTCEntityProviderServiceUtils;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.LearningResourceStoreService;

public class ArticulateTCEntityProviderServiceImpl implements ArticulateTCEntityProviderService {

    private Log log = LogFactory.getLog(ArticulateTCEntityProviderServiceImpl.class);

    private LearningResourceStoreService learningResourceStoreService;

    public void init() {
        learningResourceStoreService = (LearningResourceStoreService) ComponentManager.get("org.sakaiproject.event.api.LearningResourceStoreService");
    }

    @Override
    public String processContentPayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String payload = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        String contentStr = ArticulateTCEntityProviderServiceUtils.getContentFromPayload(payload);

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
