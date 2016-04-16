package org.sakaiproject.articulate.tincan.api;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;

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
    void postStatePayload(HttpServletRequest request);

    /**
     * 1. Retrieve the content payload from the request
     * 2. Get the data from the payload as a {@link ArticulateTCActivityState} object
     * 3. Retrieve the activity state from the database
     * 
     * @param request the {@link HttpServletRequest} object
     * @return the state data as a JSON object string
     */
    String getStatePayload(HttpServletRequest request);

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

}
