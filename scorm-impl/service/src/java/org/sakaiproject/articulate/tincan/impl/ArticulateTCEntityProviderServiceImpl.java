package org.sakaiproject.articulate.tincan.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCActivityStateDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;
import org.sakaiproject.articulate.tincan.util.ArticulateTCEntityProviderServiceUtils;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.LearningResourceStoreService;

public class ArticulateTCEntityProviderServiceImpl implements ArticulateTCEntityProviderService {

    private Log log = LogFactory.getLog(ArticulateTCEntityProviderServiceImpl.class);

    @Setter
    private ArticulateTCActivityStateDao articulateTCActivityStateDao;

    private LearningResourceStoreService learningResourceStoreService;

    public void init() {
        learningResourceStoreService = (LearningResourceStoreService) ComponentManager.get("org.sakaiproject.event.api.LearningResourceStoreService");
    }

    @Override
    public void postStatementPayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String payload = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        String statementJson = ArticulateTCEntityProviderServiceUtils.getContentDataFromPayload(payload);

        sendStatementToLRS(statementJson);
    }

    @Override
    public void postStatePayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String payload  = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        Map<String, String> content = ArticulateTCEntityProviderServiceUtils.getStateDataFromPayload(payload);
        ArticulateTCActivityState articulateTCActivityState = new ArticulateTCActivityState(content);

        articulateTCActivityStateDao.save(articulateTCActivityState);
    }

    @Override
    public String getStatePayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String stateData = null;

        String payload  = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        Map<String, String> content = ArticulateTCEntityProviderServiceUtils.getStateDataFromPayload(payload);
        ArticulateTCActivityState articulateTCActivityState = new ArticulateTCActivityState(content);

        articulateTCActivityState = articulateTCActivityStateDao.get(articulateTCActivityState.getId());

        if (articulateTCActivityState != null) {
            stateData = articulateTCActivityState.getContent();
        }

        return stateData;
    }

    @Override
    public void deleteStateData(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }


        String payload  = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        Map<String, String> content = ArticulateTCEntityProviderServiceUtils.getStateDataFromPayload(payload);
        ArticulateTCActivityState articulateTCActivityState = new ArticulateTCActivityState(content);

        articulateTCActivityStateDao.remove(articulateTCActivityState);
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
