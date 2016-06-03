package org.sakaiproject.articulate.tincan.api;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.service.gradebook.shared.Assignment;

public interface ArticulateTCConfigurationService {

    /**
     * Adds a new gradebook item
     * 
     * @param articulateTCContentPackage
     * @return
     */
    boolean addGradebookItem(ArticulateTCContentPackage articulateTCContentPackage);

    /**
     * Updates an existing gradebook item
     * 
     * @param articulateTCContentPackage
     * @param assignment
     * @return
     */
    boolean updateGradebookItem(ArticulateTCContentPackage articulateTCContentPackage, Assignment assignment);

    /**
     * Removes an existing gradebook item
     * 
     * @param articulateTCContentPackage
     * @param assignment
     * @return
     */
    boolean removeGradebookItem(ArticulateTCContentPackage articulateTCContentPackage, Assignment assignment);

    /**
     * Gets the gradebook item
     * 
     * @param articulateTCContentPackage
     * @return
     */
    Assignment getAssignment(ArticulateTCContentPackage articulateTCContentPackage);

    /**
     * Is there a gradebook defined in site?
     * 
     * @param articulateTCContentPackage
     * @return
     */
    boolean isGradebookDefined(ArticulateTCContentPackage articulateTCContentPackage);

    /**
     * Gets the content package
     * 
     * @param contentPackageId
     * @return
     */
    ArticulateTCContentPackage getContentPackage(long contentPackageId);

}
