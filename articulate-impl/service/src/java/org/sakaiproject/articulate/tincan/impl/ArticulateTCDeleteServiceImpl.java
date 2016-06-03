package org.sakaiproject.articulate.tincan.impl;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCDeleteService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.articulate.tincan.util.ArticulateTCContentEntityUtils;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.service.gradebook.shared.GradebookService;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCDeleteServiceImpl implements ArticulateTCDeleteService, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCDeleteServiceImpl.class);

    @Setter
    private ArticulateTCContentEntityUtils articulateTCContentEntityUtils;

    @Setter
    private ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @Setter
    private ContentHostingService contentHostingService;

    @Setter
    private DeveloperHelperService developerHelperService;

    @Setter
    private EventTrackingService eventTrackingService;

    @Setter
    private GradebookService gradebookService;

    @Override
    public boolean deleteContentPackage(Long contentPackageId) {
        try {
            ArticulateTCContentPackage articulateTCContentPackage = retrieveContentPackage(contentPackageId);

            if (articulateTCContentPackage == null) {
                // no content package, skip deletion
                return true;
            }

            articulateTCContentPackageDao.remove(articulateTCContentPackage);

            eventTrackingService.post(eventTrackingService.newEvent(SAKAI_EVENT_REMOVE, "articulate/tc/site/" + articulateTCContentPackage.getContext() + "/user/" + developerHelperService.getCurrentUserId() + "/packageId/" + articulateTCContentPackage.getContentPackageId(), true));
        } catch (Exception e) {
            log.error("Error deleting content package with ID: " + contentPackageId, e);
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteGradebookItem(Long contentPackageId) {
        if (contentPackageId == null) {
            throw new IllegalArgumentException("Content package ID cannot be null");
        }

        Long assignmentId = null;

        try {
            ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.load(contentPackageId);

            if (articulateTCContentPackage == null) {
                // no package settings data found, skip deletion
                log.debug("No content package settings found for ID: " + contentPackageId);
                return true;
            }

            assignmentId = articulateTCContentPackage.getAssignmentId();

            if (assignmentId == null) {
                // no assignment exists, skip deletion
                return true;
            }

            gradebookService.removeAssignment(assignmentId);
        } catch (Exception e) {
            log.error("Error deleting assignment with ID: " + assignmentId, e);
        }
        return true;
    }

    @Override
    public boolean deleteResourceFiles(Long contentPackageId) {
        ArticulateTCContentPackage articulateTCContentPackage = retrieveContentPackage(contentPackageId);

        if (articulateTCContentPackage == null) {
            // no content package, skip deletion
            return true;
        }

        try {
            developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);

            String resourcePath = StringUtils.removeStart(articulateTCContentPackage.getUrl(), ARCHIVE_DEFAULT_URL_PATH_PREFIX);
            ContentResourceEdit resourceEdit = articulateTCContentEntityUtils.editResource(resourcePath);

            if (resourceEdit == null) {
                return true;
            }

            ContentCollection collection = resourceEdit.getContainingCollection();
            contentHostingService.cancelResource(resourceEdit);

            if (collection == null) {
                return true;
            }

            // remove the collection
            contentHostingService.removeCollection(collection.getId());
        } catch (Exception e) {
            log.error("Error deleting resource files for package ID: " + contentPackageId);
            return false;
        } finally {
            developerHelperService.restoreCurrentUser();
        }

        return true;
    }

    /**
     * Retrieves the content package object with the given ID
     * 
     * @param contentPackageId
     * @return the {@link ArticulateTCContentPackage} with the given ID or null if one doesn't exist
     */
    private ArticulateTCContentPackage retrieveContentPackage(Long contentPackageId) {
        if (contentPackageId == null) {
            throw new IllegalArgumentException("Content package ID cannot be null");
        }

        ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.load(contentPackageId);

        if (articulateTCContentPackage == null) {
            // no content package with the given ID
            log.debug("No content package found for ID: " + contentPackageId);
        }

        return articulateTCContentPackage;
    }

}
