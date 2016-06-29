package org.sakaiproject.articulate.tincan.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCActivityStateDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptResultDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.articulate.tincan.util.ArticulateTCEntityProviderServiceUtils;
import org.sakaiproject.articulate.tincan.util.ArticulateTCJsonUtils;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.LearningResourceStoreService;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.CommentDefinition;
import org.sakaiproject.service.gradebook.shared.GradeDefinition;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCEntityProviderServiceImpl implements ArticulateTCEntityProviderService, ArticulateTCConstants {

    private final Logger log = LoggerFactory.getLogger(ArticulateTCEntityProviderServiceImpl.class);

    @Setter
    private ArticulateTCActivityStateDao articulateTCActivityStateDao;

    @Setter
    private ArticulateTCAttemptDao articulateTCAttemptDao;

    @Setter
    private ArticulateTCAttemptResultDao articulateTCAttemptResultDao;

    @Setter
    private ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @Setter
    private DeveloperHelperService developerHelperService;

    @Setter
    private EventTrackingService eventTrackingService;

    @Setter
    private ServerConfigurationService serverConfigurationService;

    @Setter
    private SiteService siteService;

    @Setter
    private UserDirectoryService userDirectoryService;

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

        ArticulateTCRequestPayload articulateTCRequestPayload = ArticulateTCEntityProviderServiceUtils.getPayloadObject(request);
        ArticulateTCAttempt articulateTCAttempt = articulateTCAttemptDao.lookupNewest(articulateTCRequestPayload.getContentPackageId(), articulateTCRequestPayload.getUserId());
        ArticulateTCActivityState articulateTCActivityState = null;

        if (articulateTCAttempt != null) {
            articulateTCActivityState = articulateTCActivityStateDao.findOneByUniqueKey(articulateTCAttempt.getId());
        }

        if (articulateTCActivityState == null) {
            // row does not exist, create a new one
            articulateTCActivityState = new ArticulateTCActivityState(articulateTCRequestPayload, articulateTCAttempt.getId());
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

        ArticulateTCRequestPayload articulateTCRequestPayload = ArticulateTCEntityProviderServiceUtils.getPayloadObject(request);
        ArticulateTCAttempt articulateTCAttempt = articulateTCAttemptDao.lookupNewest(articulateTCRequestPayload.getContentPackageId(), articulateTCRequestPayload.getUserId());
        ArticulateTCActivityState articulateTCActivityState = null;

        if (articulateTCAttempt == null) {
            return null;
        }

        articulateTCActivityState = articulateTCActivityStateDao.findOneByUniqueKey(articulateTCAttempt.getId());

        if (articulateTCActivityState == null) {
            // find previous launch attempt state data, if available
            if (articulateTCAttempt.getAttemptNumber() <= 1) {
                return null;
            }

            ArticulateTCAttempt previousArticulateTCAttempt = articulateTCAttemptDao.lookup(articulateTCRequestPayload.getContentPackageId(), articulateTCRequestPayload.getUserId(), articulateTCAttempt.getAttemptNumber() - 1);

            if (previousArticulateTCAttempt == null) {
                return null;
            }

            articulateTCActivityState = articulateTCActivityStateDao.findOneByUniqueKey(previousArticulateTCAttempt.getId());

            if (articulateTCActivityState == null) {
                return null;
            }

            if (!articulateTCActivityState.isResume()) {
                return null;
            }
        }

        return articulateTCActivityState.getContent();
    }

    @Override
    public void sendStatementToLRS(String statementJson) {
        if (StringUtils.isBlank(statementJson)) {
            throw new IllegalArgumentException("Statement JSON cannot be empty.");
        }

        try {
            User user = userDirectoryService.getCurrentUser();
            Map<String, Object> statementMap = ArticulateTCJsonUtils.parseFromJsonObject(statementJson);
            String identifierKey = serverConfigurationService.getString("lrs.tincanapi.inverse.functional.identifier", "account");

            Map<String, Object> actorMap = new HashMap<String, Object>();
            actorMap.put("name", user.getDisplayName());
            actorMap.put("objectType", "Agent");

            if (StringUtils.equalsIgnoreCase(identifierKey, "mbox")) {
                actorMap.put("mbox", "mailto:" + user.getEmail());
            } else if (StringUtils.equalsIgnoreCase(identifierKey, "mbox_sha1sum")) {
                actorMap.put("mbox_sha1sum", DigestUtils.sha1Hex("mailto:" + user.getEmail()));
            } else if (StringUtils.equalsIgnoreCase(identifierKey, "openid")) {
                // NOT IMPLEMENTED
                actorMap.put("openid", "NOT_IMPLEMENTED");
            } else {
                // default identifier is account object
                Map<String, String> accountMap = new HashMap<String, String>(2);
                accountMap.put("name", user.getEid());
                accountMap.put("homePage", serverConfigurationService.getServerUrl());
                actorMap.put("account", accountMap);
            }

            statementMap.put("actor", actorMap);
            statementJson = ArticulateTCJsonUtils.parseToJson(statementMap);

            LearningResourceStoreService.LRS_Statement statement = new LearningResourceStoreService.LRS_Statement(statementJson);
            learningResourceStoreService.registerStatement(statement, null);
        } catch (Exception e) {
            log.error("Error sending statement to LRS :: Statement JSON: \n {}", statementJson, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processGradebookData(String statementJson, String payload) {
        try {
            /*
             * Get the attempt and package objects
             */

            ArticulateTCRequestPayload articulateTCRequestPayload = ArticulateTCEntityProviderServiceUtils.getPayloadObject(payload);
            ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.load(articulateTCRequestPayload.getContentPackageId());

            developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);

            if (articulateTCContentPackage == null) {
                // no content package
                return;
            }

            if (!articulateTCContentPackage.isGraded()) {
                // is not set to be graded
                return;
            }

            boolean isGradebookDefined = gradebookService.isGradebookDefined(articulateTCRequestPayload.getSiteId());
            if (!isGradebookDefined) {
                // no gradebook defined in the site
                return;
            }

            Assignment assignment = gradebookService.getAssignment(articulateTCRequestPayload.getSiteId(), articulateTCContentPackage.getAssignmentId());
            if (assignment == null) {
                // assignment is not defined in gradebook
                return;
            }

            /*
             * Retrieve data from the objects
             */

            Map<String, Object> statement = (Map<String, Object>) ArticulateTCJsonUtils.parseFromJsonObject(statementJson);
            if (statement.isEmpty()) {
                // no JSON object
                return;
            }

            Map<String, Object> result = (Map<String, Object>) statement.get("result");
            if (result == null) {
                // no result in content
                return;
            }

            Boolean completion = (Boolean) result.get("completion");
            if (completion == null || !completion) {
                // activity is not completed
                return;
            }

            Map<String, Object> score = (Map<String, Object>) result.get("score");
            if (score == null) {
                // no score sent
                return;
            }

            Double scaled = (Double) score.get("scaled");
            if (scaled == null) {
                // no scale sent, default to 0 (zero)
                scaled = 0d;
            }

            Double assignmentPoints = assignment.getPoints();

            /*
             * Save the Attempt result
             */

            Double attemptScore = (assignmentPoints != null) ? assignmentPoints * scaled : 0d;

            saveAttemptResult(articulateTCContentPackage.getContentPackageId(), articulateTCRequestPayload.getUserId(), scaled);

            /*
             * Update the activity state ID to "complete"
             */

            ArticulateTCAttempt articulateTCAttempt = articulateTCAttemptDao.lookupNewest(articulateTCContentPackage.getContentPackageId(), articulateTCRequestPayload.getUserId());

            if (articulateTCAttempt != null) {
                updateActivityStateId(articulateTCAttempt.getId(), STATE_DATA_KEY_STATE_ID_COMPLETE);
            }

            /*
             * Save grade to gradebook
             */

            if (!allowedToPostAttemptGrade(articulateTCContentPackage.getContentPackageId(), articulateTCRequestPayload.getUserId())) {
                // this attempt is greater than the allowed max attempt number
                return;
            }

            String previousScoreStr = gradebookService.getAssignmentScoreString(articulateTCRequestPayload.getSiteId(), assignment.getName(), articulateTCRequestPayload.getUserId());

            if (StringUtils.isNotBlank(previousScoreStr)) {
                Double previousScore = Double.parseDouble(previousScoreStr);

                if (articulateTCContentPackage.isRecordBest()) {
                    // only record the best attempt score
                    if (attemptScore <= previousScore) {
                        // configured to only record best score and score is not better than previous, skip saving to gradebook
                        return;
                    }
                }
            }

            GradeDefinition gradeDefinition = new GradeDefinition();
            gradeDefinition.setDateRecorded(new Date());
            gradeDefinition.setGrade(Double.toString(attemptScore));
            gradeDefinition.setStudentUid(articulateTCRequestPayload.getUserId());
            gradeDefinition.setGraderUid(articulateTCContentPackage.getCreatedBy());

            List<GradeDefinition> gradeDefinitions = new ArrayList<>();
            gradeDefinitions.add(gradeDefinition);

            gradebookService.saveGradesAndComments(articulateTCRequestPayload.getSiteId(), assignment.getId(), gradeDefinitions);

            eventTrackingService.post(eventTrackingService.newEvent(SAKAI_EVENT_GRADE, "articulate/tc/site/" + articulateTCContentPackage.getContext() + "/instructor/" + articulateTCContentPackage.getCreatedBy() + "/student/" + articulateTCRequestPayload.getUserId() + "/packageId/" + articulateTCContentPackage.getContentPackageId(), true));
        } catch (Exception e) {
            log.error("Error sending grade to gradebook.", e);
        } finally {
            developerHelperService.restoreCurrentUser();
        }
    }

    @Override
    public boolean allowedToPostAttemptGrade(long contentPackageId, String userId) {
        ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(contentPackageId);
        long maxAttempts = articulateTCContentPackage.getNumberOfTries();

        if (articulateTCContentPackage.isUnlimitedAttempts()) {
            // unlimited attempts allowed
            return true;
        }

        ArticulateTCAttempt newestAttempt = articulateTCAttemptDao.lookupNewest(contentPackageId, userId);

        if (newestAttempt == null) {
            // no prior attempts
            return true;
        }

        return newestAttempt.getAttemptNumber() <= maxAttempts;
    }

    @Override
    public void saveAttemptResult(long contentPackageId, String userId, Double scaledScore) {
        ArticulateTCAttempt newestAttempt = articulateTCAttemptDao.lookupNewest(contentPackageId, userId);

        if (newestAttempt == null) {
            // should not be here, as an attempt was created on launch
            log.error("Error: no attempt found for content package id: {} and user ID: {}", contentPackageId, userId);
            return;
        }

        ArticulateTCAttemptResult articulateTCAttemptResult = articulateTCAttemptResultDao.findByAttemptIdNumber(newestAttempt.getId(), newestAttempt.getAttemptNumber());

        if (articulateTCAttemptResult == null) {
            articulateTCAttemptResult = new ArticulateTCAttemptResult();
            articulateTCAttemptResult.setAttemptId(newestAttempt.getId());
        }

        articulateTCAttemptResult.setAttemptNumber(newestAttempt.getAttemptNumber());
        articulateTCAttemptResult.setScaledScore(scaledScore);
        articulateTCAttemptResult.setDateCompleted(new Date());

        articulateTCAttemptResultDao.save(articulateTCAttemptResult);
    }

    @Override
    public void updateScaledScores(String gradebookUid, long assignmentId, Double currentPoints) {
        Assignment assignment = gradebookService.getAssignment(gradebookUid, assignmentId);

        if (assignment == null) {
            // no assignment found in this gradebook
            return;
        }

        try {
            String siteId = developerHelperService.getCurrentLocationId();
            Map<String, String> studentsToGrade = gradebookService.getViewableStudentsForItemForCurrentUser(gradebookUid, assignmentId);

            for (Map.Entry<String, String> student : studentsToGrade.entrySet()) {
                if (StringUtils.equalsIgnoreCase(student.getValue(), "grade")) {
                    String previousScoreStr = gradebookService.getAssignmentScoreString(siteId, assignment.getId(), student.getKey());

                    if (StringUtils.isBlank(previousScoreStr)) {
                        // no current score, move on to next user
                        continue;
                    }

                    Double previousScore = Double.parseDouble(previousScoreStr);
                    Double previousScale = previousScore / currentPoints;
                    String newScore = GRADE_DECIMAL_FORMAT.format(assignment.getPoints() * previousScale);

                    CommentDefinition cd = gradebookService.getAssignmentScoreComment(siteId, assignment.getId(), student.getKey());
                    String comment = cd != null ? cd.getCommentText() : "";

                    gradebookService.saveGradeAndCommentForStudent(gradebookUid, assignmentId, student.getKey(), newScore, comment);
                }
            }
        } catch (Exception e) {
            // an error occurred, so abort saving assignment scores
            log.error("Error occurred saving grades for assignment ID: {} in gradebook: {}", assignmentId, gradebookUid, e);
            return;
        }

    }

    @Override
    public void updateActivityStateId(long attemptId, String stateId) {
        ArticulateTCActivityState articulateTCActivityState = articulateTCActivityStateDao.findOneByUniqueKey(attemptId);

        if (articulateTCActivityState == null) {
            // no activity state exists, return
            return;
        }

        articulateTCActivityState.setStateId(stateId);

        articulateTCActivityStateDao.save(articulateTCActivityState);
    }

}
