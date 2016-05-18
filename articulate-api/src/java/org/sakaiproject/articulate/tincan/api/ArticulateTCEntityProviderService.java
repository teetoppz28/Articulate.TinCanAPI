package org.sakaiproject.articulate.tincan.api;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public interface ArticulateTCEntityProviderService {

    /**
     * 1. Retrieve the content payload from the request
     * 2. Get the JSON from the content variable
     * 3. Send the JSON statement to the LRS
     * 
     * @param request the {@link HttpServletRequest} object
     * @return the statement JSON string
     */
    void postStatementPayload(HttpServletRequest request);

    /**
     * 1. Retrieve the content payload from the request
     * 2. Get the data from the payload as a {@link ArticulateTCActivityState} object
     * 3. Save the activity state to the database
     * 
     * @param request the {@link HttpServletRequest} object
     * @return the {@link ArticulateTCActivityState} object
     */
    void postActivityStatePayload(HttpServletRequest request);

    /**
     * 1. Retrieve the content payload from the request
     * 2. Get the data from the payload as a {@link ArticulateTCActivityState} object
     * 3. Retrieve the activity state from the database
     * 
     * @param request the {@link HttpServletRequest} object
     * @return the state data as a JSON object string
     */
    String getActivityStatePayload(HttpServletRequest request);

    /**
     * 1. Retrieve the content payload from the request
     * 2. Get the data from the payload as a {@link ArticulateTCActivityState} object
     * 3. Delete the activity state from the database
     * 
     * @param request the {@link HttpServletRequest} object
     * @return the state data as a JSON object string
     */
    void deleteStateData(HttpServletRequest request);

    /**
     * Send the statement JSON to the configured LRS
     * 
     * @param statementJson the statement JSON string
     */
    void sendStatementToLRS(String statementJson);

    /**
     * Process the result statement to gradebook
     * @throws Exception 
     */
    void processGradebookData(String statementJson, String payload) throws Exception;

    /**
     * Is this attempt allowed to store its grade in the database?
     * 
     * @param contentPackageId
     * @param userId
     * @return true, if the attempt number is less than or equal to the configured max attempt count
     */
    boolean allowedToPostAttemptGrade(long contentPackageId, String userId);

    /**
     * Persist the attempt score
     */
    void saveAttemptResult(long contentPackageId, String userId, Double score);

    /**
     * Update the scores using the scaled score, if the points in the configuration has changed
     * 
     * @param gradebookUid the UID of the gradebook
     * @param assignmentId the ID of the assignment item
     * @param currentPoints
     */
    void updateScaledScores(String gradebookUid, long assignmentId, Double currentPoints);

}
