package org.sakaiproject.articulate.tincan.api;

import java.util.List;
import java.util.Map;

import org.sakaiproject.articulate.tincan.model.ArticulateTCMemberAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;

public interface ArticulateTCResultsService {

    /**
     * Calculates the user's ID and full name for display
     * 
     * @param userIdParam
     * @param fullNameParam
     * @return
     */
    Map<String, String> calculateUserData(String userIdParam, String fullNameParam);

    /**
     * Calculates the gradebook score
     * 
     * @param contentPackageId
     * @param gradebookScore
     * @param userId
     * @return
     */
    String calculateGradebookScore(String contentPackageId, String gradebookScore, String userId);

    /**
     * Calculates the total points possible
     * 
     * @param contentPackageId
     * @param gradebookPointsPossible
     * @return
     */
    String calculatePointsPossible(String contentPackageId, String gradebookPointsPossible);

    /**
     * Calculates the attempt results for the content package
     * 
     * @param contentPackageId
     * @param userId
     * @return
     */
    Map<String, List<ArticulateTCAttemptResult>> calculateAttemptResults(String contentPackageId, String userId);

    /**
     * Calculates all attempts for each user in the site
     * 
     * @param siteId
     * @param contentPackageId
     * @param assignmentId
     * @return
     */
    List<ArticulateTCMemberAttemptResult> calculateLearnerAttemptResults(String siteId, Long contentPackageId, Long assignmentId);

}
