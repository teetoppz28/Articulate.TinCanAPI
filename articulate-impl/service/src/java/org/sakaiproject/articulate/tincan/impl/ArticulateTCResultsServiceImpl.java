package org.sakaiproject.articulate.tincan.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.api.ArticulateTCResultsService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptResultDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;

public class ArticulateTCResultsServiceImpl implements ArticulateTCResultsService, ArticulateTCConstants {

    @Setter
    private ArticulateTCAttemptDao articulateTCAttemptDao;

    @Setter
    private ArticulateTCAttemptResultDao articulateTCAttemptResultDao;

    @Setter
    private ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @Setter
    private ArticulateTCEntityProviderService articulateTCEntityProviderService;

    @Setter
    private DeveloperHelperService developerHelperService;

    @Setter
    private GradebookService gradebookService;

    @Override
    public String calculatePointsPossible(String contentPackageId, String gradebookPointsPossible) {
        if (StringUtils.isBlank(gradebookPointsPossible)) {
            gradebookPointsPossible = "-";
            ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(Long.parseLong(contentPackageId));

            if (articulateTCContentPackage != null) {
                Long assignmentId = articulateTCContentPackage.getAssignmentId();

                if (assignmentId != null) {
                    Assignment assignment = gradebookService.getAssignment(articulateTCContentPackage.getContext(), assignmentId);

                    if (assignment != null) {
                        Double pointsPossible = assignment.getPoints();
 
                        if (pointsPossible != null) {
                            gradebookPointsPossible = Double.toString(pointsPossible);
                        }
                    }
                }
            }
        }

        return gradebookPointsPossible;
    }

    @Override
    public String calculateGradebookScore(String contentPackageId, String gradebookScore, String userId) {
        if (StringUtils.isBlank(gradebookScore)) {
            gradebookScore = "N/A";
            ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(Long.parseLong(contentPackageId));

            if (articulateTCContentPackage != null) {
                if (articulateTCContentPackage.getAssignmentId() != null) {
                    try {
                        developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);
                        gradebookScore = gradebookService.getAssignmentScoreString(articulateTCContentPackage.getContext(), articulateTCContentPackage.getAssignmentId(), userId);
                    } catch (Exception e) {
                        // log.error("Error getting score string for user {} in site {} and asignment {}", userId, articulateTCContentPackage.getContext(), articulateTCContentPackage.getAssignmentId(), e);
                    } finally {
                        developerHelperService.restoreCurrentUser();
                    }
                }
            }
        }

        return gradebookScore;
    }

    @Override
    public Map<String, List<ArticulateTCAttemptResult>> calculateAttemptResults(String contentPackageId, String userId) {
        List<ArticulateTCAttemptResult> articulateTCAttemptResultsComplete = new ArrayList<>();
        List<ArticulateTCAttemptResult> articulateTCAttemptResultsIncomplete = new ArrayList<>();
        Map<String, List<ArticulateTCAttemptResult>> results = new HashMap<>(2);

        List<ArticulateTCAttempt> articulateTCAttempts = articulateTCAttemptDao.find(Long.parseLong(contentPackageId), userId);

        for (ArticulateTCAttempt articulateTCAttempt : articulateTCAttempts) {
            List<ArticulateTCAttemptResult> complete = articulateTCAttemptResultDao.findByAttemptId(articulateTCAttempt.getId());

            if (complete != null) {
                articulateTCAttemptResultsComplete.addAll(complete);
            }

            List<ArticulateTCAttemptResult> incomplete = articulateTCAttemptResultDao.findByAttemptIdIncomplete(articulateTCAttempt.getId());

            if (incomplete != null) {
                articulateTCAttemptResultsIncomplete.addAll(incomplete);
            }
        }

        results.put("complete", articulateTCAttemptResultsComplete);
        results.put("incomplete", articulateTCAttemptResultsIncomplete);

        return results;
    }

}
