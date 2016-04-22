package org.sakaiproject.articulate.tincan.api;

public interface ArticulateTCLaunchService {

    /**
     * Creates the necessary launch query string
     * 1. endpoint
     * 2. auth
     * 3. actor
     * 4. site ID
     * 5. user ID
     * 
     * @param packageId the ID of the content package row
     * @return the URL encoded query string
     */
    public String calculateLaunchParams(String packageId);

    /**
     * Creates the actor definition
     * e.g. %7B\"name\"%3A%20%5B\"First%20Last\"%5D%2C%20\"mbox\"%3A%20%5B\"mailto%3Afirstlast%40email.com\"%5D%7D"
     * 
     * @return the URL encoded actor string
     */
    public String calculateActor();

    /**
     * Gets the current site
     * 
     * @return the URL encoded site ID
     */
    public String calculateCurrentSite();

    /**
     * Gets the current user
     * 
     * @return the URL encoded user ID
     */
    public String calculateCurrentUserId();

    /**
     * Creates the endpoint URL for LRS statements
     * e.g. %2Fdirect%2Ftincanapi-lrs%2Faction%2F
     * 
     * @return the URL encoded statement URL
     */
    public String calculateEndPoint();

    /**
     * Persists a new attempt when the content package is launched
     * 
     * @param contentPackageId
     */
    public void addAttempt(String contentPackageId);

}
