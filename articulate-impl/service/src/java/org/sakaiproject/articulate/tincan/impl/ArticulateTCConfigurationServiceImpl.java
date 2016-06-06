package org.sakaiproject.articulate.tincan.impl;

import lombok.Setter;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCConfigurationService;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;

public class ArticulateTCConfigurationServiceImpl implements ArticulateTCConfigurationService, ArticulateTCConstants {

    @Setter
    private ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @Setter
    private ArticulateTCEntityProviderService articulateTCEntityProviderService;

    @Setter
    private GradebookService gradebookService;

    @Override
    public boolean addGradebookItem(ArticulateTCContentPackage articulateTCContentPackage) {
        String fixedTitle = getItemTitle(articulateTCContentPackage, articulateTCContentPackage.getContext());
        Double points = articulateTCContentPackage.getPoints();

        Assignment assignment = new Assignment();
        assignment.setName(fixedTitle);
        assignment.setDueDate(articulateTCContentPackage.getDueOn());
        assignment.setPoints(points);
        assignment.setCounted(true);

        gradebookService.addAssignment(articulateTCContentPackage.getContext(), assignment);

        // sync the assignment IDs
        assignment = gradebookService.getAssignment(articulateTCContentPackage.getContext(), assignment.getName());
        articulateTCContentPackage.setAssignmentId(assignment.getId());

        articulateTCContentPackageDao.save(articulateTCContentPackage);

        return true;
    }

    @Override
    public boolean updateGradebookItem(ArticulateTCContentPackage articulateTCContentPackage, Assignment assignment) {
        Double previousPoints = assignment.getPoints();
        Double points = articulateTCContentPackage.getPoints();

        assignment.setDueDate(articulateTCContentPackage.getDueOn());
        assignment.setPoints(points);

        gradebookService.updateAssignment(articulateTCContentPackage.getContext(), assignment.getName(), assignment);

        // update the scores for the new points, if changed
        if (previousPoints != points) {
            articulateTCEntityProviderService.updateScaledScores(articulateTCContentPackage.getContext(), assignment.getId(), previousPoints);
        }

        articulateTCContentPackageDao.save(articulateTCContentPackage);

        return true;
    }

    @Override
    public boolean removeGradebookItem(ArticulateTCContentPackage articulateTCContentPackage, Assignment assignment) {
        gradebookService.removeAssignment(assignment.getId());
        // reset gradebook item title to package title
        articulateTCContentPackage.setGradebookItemTitle(articulateTCContentPackage.getTitle());
        // reset gradebook item points to default
        articulateTCContentPackage.setPoints(CONFIGURATION_DEFAULT_POINTS);
        // reset the assignment ID
        articulateTCContentPackage.setAssignmentId(null);

        articulateTCContentPackageDao.save(articulateTCContentPackage);

        return true;
    }

    /**
     * Generates the gradebook item title, not allowing duplicates
     * 
     * @param articulateTCContentPackage
     * @param context
     * @return
     */
    private String getItemTitle(ArticulateTCContentPackage articulateTCContentPackage, String context) {
        String fixedTitle = articulateTCContentPackage.getGradebookItemTitle();

        int count = 1;

        while (gradebookService.isAssignmentDefined(context, fixedTitle)) {
            fixedTitle = articulateTCContentPackage.getGradebookItemTitle() + " (" + count++ + ")";
        }

        return fixedTitle;
    }

    @Override
    public Assignment getAssignment( ArticulateTCContentPackage articulateTCContentPackage) {
        return gradebookService.getAssignment(articulateTCContentPackage.getContext(), articulateTCContentPackage.getAssignmentId());
    }

    @Override
    public boolean isGradebookDefined( ArticulateTCContentPackage articulateTCContentPackage) {
        return gradebookService.isGradebookDefined(articulateTCContentPackage.getContext());
    }

    @Override
    public ArticulateTCContentPackage getContentPackage(long contentPackageId) {
        return articulateTCContentPackageDao.load(contentPackageId);
    }

}
