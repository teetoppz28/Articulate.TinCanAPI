package org.sakaiproject.articulate.tincan.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.api.ArticulateTCResultsService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptResultDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMemberAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticulateTCResultsServiceImpl implements ArticulateTCResultsService, ArticulateTCConstants {

    private final Logger log = LoggerFactory.getLogger(ArticulateTCResultsServiceImpl.class);

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

    @Setter
    private SiteService siteService;

    @Setter
    private UserDirectoryService userDirectoryService;

    @Override
    public String calculatePointsPossible(String contentPackageId, String gradebookPointsPossible) {
        if (StringUtils.isBlank(gradebookPointsPossible)) {
            gradebookPointsPossible = CONFIGURATION_GRADEBOOK_NO_POINTS;
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
            gradebookScore = CONFIGURATION_GRADEBOOK_NOT_AVAILABLE;
            ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(Long.parseLong(contentPackageId));

            if (articulateTCContentPackage != null) {
                if (articulateTCContentPackage.getAssignmentId() != null) {
                    try {
                        developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);
                        gradebookScore = gradebookService.getAssignmentScoreString(articulateTCContentPackage.getContext(),
                            articulateTCContentPackage.getAssignmentId(), userId);
                    } catch (Exception e) {
                        log.error("Error getting score string for user {} in site {} and asignment {}",
                            userId, articulateTCContentPackage.getContext(), articulateTCContentPackage.getAssignmentId(), e);
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

        results.put(RESULTS_KEY_COMPLETE, articulateTCAttemptResultsComplete);
        results.put(RESULTS_KEY_INCOMPLETE, articulateTCAttemptResultsIncomplete);

        return results;
    }

    @Override
    public List<ArticulateTCMemberAttemptResult> calculateLearnerAttemptResults(String siteId, Long contentPackageId, Long assignmentId) {
        Set<Member> members = calculateLearnerMembers(siteId);
        String gradebookPointsPossible = calculateAssignmentPoints(siteId, assignmentId);
        List<ArticulateTCMemberAttemptResult> articulateTCMemberAttemptResults = new ArrayList<>();

        for (Member member : members) {
            String firstName = "";
            String lastName = "";
            String fullName = "";
            User user = null;

            try {
                user = userDirectoryService.getUser(member.getUserId());
                firstName = user.getFirstName();
                lastName = user.getLastName();
                fullName = user.getDisplayName();
            } catch (Exception e) {
                log.error("Error retrieving data for user with ID: {}", member.getUserId(), e);
            }

            ArticulateTCAttempt articulateTCAttempt = articulateTCAttemptDao.lookupNewest(contentPackageId, member.getUserId());

            String gradebookScore = CONFIGURATION_GRADEBOOK_NO_POINTS;

            if (assignmentId != null) {
                gradebookScore = gradebookService.getAssignmentScoreString(siteId, assignmentId, member.getUserId());
            }

            ArticulateTCMemberAttemptResult articulateTCMemberAttemptResult = new ArticulateTCMemberAttemptResult();
            articulateTCMemberAttemptResult.setUserId(member.getUserId());
            articulateTCMemberAttemptResult.setEid(member.getUserEid());
            articulateTCMemberAttemptResult.setFirstName(firstName);
            articulateTCMemberAttemptResult.setLastName(lastName);
            articulateTCMemberAttemptResult.setFullName(fullName);
            articulateTCMemberAttemptResult.setAttemptNumber(articulateTCAttempt != null ? Long.toString(articulateTCAttempt.getAttemptNumber()) : CONFIGURATION_GRADEBOOK_NO_POINTS);
            articulateTCMemberAttemptResult.setGradebookScore(gradebookScore);
            articulateTCMemberAttemptResult.setGradebookPointsPossible(gradebookPointsPossible);

            articulateTCMemberAttemptResults.add(articulateTCMemberAttemptResult);
        }

        return articulateTCMemberAttemptResults;
    }

    /**
     * Get members o a site with non-maintain roles
     * 
     * @param siteId
     * @return
     */
    private Set<Member> calculateLearnerMembers(String siteId) {
        Site site = null;

        try {
            site = siteService.getSite(siteId);
        } catch (IdUnusedException e) {
            // site doesn't exist
            log.error("No site exists with ID: {}", siteId, e);
        }

        // get learners
        String maintainRole = site.getMaintainRole();
        Set<Member> members = site.getMembers();
        Iterator<Member> iterator = members.iterator();

        while (iterator.hasNext()) {
            Member member = iterator.next();

            if (StringUtils.equalsIgnoreCase(member.getRole().getId(), maintainRole)) {
                // remove maintainers
                iterator.remove();
            }
        }

        return members;
    }

    @Override
    public Map<String, String> calculateUserData(String userIdParam, String fullNameParam) {
        Map<String, String> userData = new HashMap<>(2);
        userData.put(RESULTS_KEY_USERID, userIdParam);
        userData.put(RESULTS_KEY_FULLNAME, fullNameParam);
        User user = null;

        try {
            user = userDirectoryService.getCurrentUser();
        } catch (Exception e) {
            // should never get here, but just in case
            log.error("No current user exists.");
            return userData;
        }

        if (StringUtils.isBlank(userData.get(RESULTS_KEY_FULLNAME))) {
            // no full name passed in
            userData.put(RESULTS_KEY_FULLNAME, user != null ? user.getDisplayName() : "");
        }

        if (StringUtils.isBlank(userData.get(RESULTS_KEY_USERID))) {
            // no user ID passed in
            userData.put(RESULTS_KEY_USERID, user != null ? user.getId() : "");
        }

        return userData;
    }

    /**
     * Calculates the assignment total points display string
     * 
     * @param siteId
     * @param assignmentId
     * @return
     */
    private String calculateAssignmentPoints(String siteId, Long assignmentId) {
        String assignmentPoints = CONFIGURATION_GRADEBOOK_NO_POINTS;

        if (assignmentId != null) {
            Assignment assignment = gradebookService.getAssignment(siteId, assignmentId);

            if (assignment != null) {
                Double pointsPossible = assignment.getPoints();
                assignmentPoints = Double.toString(pointsPossible);
            }
        }

        return assignmentPoints;
    }

}
