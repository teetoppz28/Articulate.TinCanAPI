package org.sakaiproject.articulate.tincan.impl;

import java.util.Iterator;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCDeleteService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageSettingsDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackageSettings;
import org.sakaiproject.articulate.tincan.util.ArticulateTCContentEntityUtils;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.service.gradebook.shared.GradebookService;

public abstract class ArticulateTCDeleteServiceImpl implements ArticulateTCDeleteService, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCDeleteServiceImpl.class);

    @Setter
    private ArticulateTCContentEntityUtils articulateTCContentEntityUtils;

    @Setter
    private ArticulateTCContentPackageSettingsDao articulateTCContentPackageSettingsDao;

    @Setter
    private ContentHostingService contentHostingService;

    @Setter
    private DeveloperHelperService developerHelperService;

    @Setter
    private GradebookService gradebookService;

    protected abstract ContentPackageDao contentPackageDao();

    @Override
    public boolean deleteContentPackage(Long contentPackageId) {
        ContentPackage contentPackage = retrieveContentPackage(contentPackageId);

        if (contentPackage == null) {
            // no content package, skip deletion
            return true;
        }

        try {
            contentPackageDao().remove(contentPackage);
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

        ArticulateTCContentPackageSettings articulateTCContentPackageSettings = articulateTCContentPackageSettingsDao.findOneByPackageId(contentPackageId);

        if (articulateTCContentPackageSettings == null) {
            // no package settings data found, skip deletion
            log.debug("No content package settings found for ID: " + contentPackageId);
            return true;
        }

        Long assignmentId = articulateTCContentPackageSettings.getGradebookItemId();

        if (assignmentId == null) {
            // no assignment exists, skip deletion
            return true;
        }

        try {
            gradebookService.removeAssignment(assignmentId);
        } catch (Exception e) {
            log.error("Error deleting assignment with ID: " + assignmentId, e);
        }
        return true;
    }

    @Override
    public boolean deleteResourceFiles(Long contentPackageId) {
        ContentPackage contentPackage = retrieveContentPackage(contentPackageId);

        if (contentPackage == null) {
            // no content package, skip deletion
            return true;
        }

        try {
            developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);

            String resourcePath = StringUtils.removeStart(contentPackage.getUrl(), ARCHIVE_DEFAULT_URL_PATH_PREFIX);

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
     * @return
     */
    private ContentPackage retrieveContentPackage(Long contentPackageId) {
        if (contentPackageId == null) {
            throw new IllegalArgumentException("Content package ID cannot be null");
        }

        ContentPackage contentPackage = contentPackageDao().load(contentPackageId);

        if (contentPackage == null) {
            // no content package with the given ID
            log.debug("No content package found for ID: " + contentPackageId);
        }

        return contentPackage;
    }

}
