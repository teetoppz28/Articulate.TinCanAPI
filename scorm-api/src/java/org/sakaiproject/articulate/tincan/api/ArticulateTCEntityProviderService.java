package org.sakaiproject.articulate.tincan.api;

import javax.servlet.http.HttpServletRequest;

public interface ArticulateTCEntityProviderService {

    /**
     * 1. Retrieve the content payload from the request
     * 2. Get the JSON from the content variable
     * 
     * @param request the {@link HttpServletRequest} object
     * @return the statement JSON string
     */
    String processContentPayload(HttpServletRequest request);

    /**
     * Send the statement JSON to the configured LRS
     * 
     * @param statementJson the statement JSON string
     */
    void sendStatementToLRS(String statementJson);

}
