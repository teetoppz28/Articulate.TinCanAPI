package org.sakaiproject.articulate.tincan.api;

public interface ArticulateTCLaunchService {

    /**
     * Creates the necessary launch query string
     * 1. endpoint
     * 2. auth
     * 3. actor
     * 
     * @param packageId the ID of the content package row
     * @return the URL encoded query string
     */
    String calculateLaunchParams(String packageId);

    /**
     * Creates the actor definition
     * e.g. %7B\"name\"%3A%20%5B\"First%20Last\"%5D%2C%20\"mbox\"%3A%20%5B\"mailto%3Afirstlast%40email.com\"%5D%7D"
     * 
     * @return the URL encoded actor string
     */
    String calculateActor();

    /**
     * Creates the endpoint URL for LRS statements
     * e.g. %2Fdirect%2Ftincanapi-lrs%2Faction%2F
     * 
     * @return the URL encoded statement URL
     */
    String calculateEndPoint();

    /**
     * Persists a new attempt when the content package is launched
     * 
     * @param contentPackageId
     */
    void addAttempt(String contentPackageId);

    /**
     * Persists a new attempt result when the content package is launched
     * 
     * @param attemptId the attempt ID
     */
    void addAttemptResult(Long attemptId);

}
