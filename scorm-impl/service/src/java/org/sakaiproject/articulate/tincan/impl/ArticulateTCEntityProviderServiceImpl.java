package org.sakaiproject.articulate.tincan.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCActivityStateDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageSettingsDao;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackageSettings;
import org.sakaiproject.articulate.tincan.util.ArticulateTCEntityProviderServiceUtils;
import org.sakaiproject.articulate.tincan.util.ArticulateTCJsonUtils;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.event.api.LearningResourceStoreService;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;

public class ArticulateTCEntityProviderServiceImpl implements ArticulateTCEntityProviderService, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCEntityProviderServiceImpl.class);

    @Setter
    private ArticulateTCActivityStateDao articulateTCActivityStateDao;
    @Setter
    private ArticulateTCContentPackageSettingsDao articulateTCContentPackageSettingsDao;
    @Setter
    private DeveloperHelperService developerHelperService;

    private GradebookService gradebookService;
    private LearningResourceStoreService learningResourceStoreService;

    public void init() {
        learningResourceStoreService = (LearningResourceStoreService) ComponentManager.get("org.sakaiproject.event.api.LearningResourceStoreService");
        gradebookService = (GradebookService)  ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService");
    }

    @Override
    public void postStatementPayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String payload = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        String statementJson = ArticulateTCEntityProviderServiceUtils.getContentDataFromPayload(payload);

        sendStatementToLRS(statementJson);

        processGradebookData(statementJson, payload);
    }

    @Override
    public void postActivityStatePayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String payload  = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        ArticulateTCRequestPayload articulateTCRequestPayload = ArticulateTCEntityProviderServiceUtils.getStateDataFromPayload(payload);
        ArticulateTCActivityState articulateTCActivityState = articulateTCActivityStateDao.findOne(articulateTCRequestPayload);

        if (articulateTCActivityState == null) {
            // row does not exist, create a new one
            articulateTCActivityState = new ArticulateTCActivityState(articulateTCRequestPayload);
        } else {
            // row exists, update mutable fields
            articulateTCActivityState.updateMutableFields(articulateTCRequestPayload);
        }

        articulateTCActivityStateDao.save(articulateTCActivityState);
    }

    @Override
    public String getActivityStatePayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String stateData = null;

        String payload  = ArticulateTCEntityProviderServiceUtils.getRequestPayload(request);
        ArticulateTCRequestPayload articulateTCRequestPayload = ArticulateTCEntityProviderServiceUtils.getStateDataFromPayload(payload);
        ArticulateTCActivityState articulateTCActivityState = articulateTCActivityStateDao.findOne(articulateTCRequestPayload);

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
        ArticulateTCRequestPayload articulateTCRequestPayload = ArticulateTCEntityProviderServiceUtils.getStateDataFromPayload(payload);
        ArticulateTCActivityState articulateTCActivityState = articulateTCActivityStateDao.findOne(articulateTCRequestPayload);

        // no row to delete
        if (articulateTCActivityState == null) {
            return;
        }

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

    @SuppressWarnings("unchecked")
    @Override
    public void processGradebookData(String statementJson, String payload) {
        ArticulateTCRequestPayload articulateTCRequestPayload = ArticulateTCEntityProviderServiceUtils.getPayloadObject(payload);
        ArticulateTCContentPackageSettings articulateTCContentPackageSettings = articulateTCContentPackageSettingsDao.findOneByPackageId(articulateTCRequestPayload.getPackageId());

        if (articulateTCContentPackageSettings == null) {
            return;
        }

        if (!articulateTCContentPackageSettings.isGraded()) {
            return;
        }

        boolean isGradebookDefined = gradebookService.isGradebookDefined(articulateTCRequestPayload.getSiteId());
        if (!isGradebookDefined) {
            return;
        }

        developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);
        List<Assignment> gbAssignments = gradebookService.getAssignments(articulateTCRequestPayload.getSiteId());
        developerHelperService.restoreCurrentUser();
        Assignment assignment = null;
        for (Assignment gbAssignment : gbAssignments) {
            if (gbAssignment.getId() == articulateTCContentPackageSettings.getGradebookItemId()) {
                assignment = gbAssignment;
            }
        }

        boolean isGradebookItemDefined = assignment != null;

        if (!isGradebookItemDefined) {
            return;
        }

        Map<String, Object> statement = (Map<String, Object>) ArticulateTCJsonUtils.parseFromJsonObject(statementJson);

        if (statement.isEmpty()) {
            return;
        }

        Map<String, Object> result = (Map<String, Object>) statement.get("result");

        if (result == null) {
            return;
        }

        Map<String, Object> score = (Map<String, Object>) result.get("score");

        if (score == null) {
            return;
        }

        Double scaled = (Double) score.get("scaled");

        if (scaled == null) {
            scaled = 0d;
        }

        Double assignmentPoints = assignment.getPoints();
        Double studentPoints = assignmentPoints * scaled;

        gradebookService.setAssignmentScoreString(articulateTCRequestPayload.getSiteId(), assignment.getName(), articulateTCRequestPayload.getUserId(), Double.toString(studentPoints), CONFIGURATION_DEFAULT_GRADEBOOK_EXTERNAL_APP);
    }

    protected String buildExternalId(String siteId, String packageId) {
        return siteId + STATE_DATA_ID_SEPARATOR + packageId;
    }

}
