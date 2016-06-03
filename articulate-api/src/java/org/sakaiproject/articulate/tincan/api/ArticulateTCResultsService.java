package org.sakaiproject.articulate.tincan.api;

import java.util.List;
import java.util.Map;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;

public interface ArticulateTCResultsService {

    String calculateGradebookScore(String contentPackageId, String gradebookScore, String userId);

    String calculatePointsPossible(String contentPackageId, String gradebookPointsPossible);

    Map<String, List<ArticulateTCAttemptResult>> calculateAttemptResults(String contentPackageId, String userId);

}
