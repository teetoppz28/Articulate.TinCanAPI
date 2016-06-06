package org.sakaiproject.articulate.tincan.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.articulate.tincan.model.ArticulateTCMemberAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.authz.api.Member;

public interface ArticulateTCResultsService {

    String calculateGradebookScore(String contentPackageId, String gradebookScore, String userId);

    String calculatePointsPossible(String contentPackageId, String gradebookPointsPossible);

    Map<String, List<ArticulateTCAttemptResult>> calculateAttemptResults(String contentPackageId, String userId);

    List<ArticulateTCMemberAttemptResult> calculateLearnerAttemptResults(String siteId, Long contentPackageId, Long assignmentId);

}
