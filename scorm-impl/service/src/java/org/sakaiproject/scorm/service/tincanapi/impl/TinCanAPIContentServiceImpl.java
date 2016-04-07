package org.sakaiproject.scorm.service.tincanapi.impl;

import java.io.InputStream;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.util.ZipContentUtil;
import org.sakaiproject.entity.api.Edit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.scorm.api.ScormConstants;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIManifest;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.sakaiproject.scorm.service.tincanapi.api.TinCanAPIContentService;

public abstract class TinCanAPIContentServiceImpl extends ZipContentUtil implements TinCanAPIContentService, ScormConstants {

    private Log log = LogFactory.getLog(TinCanAPIContentServiceImpl.class);

    @Setter
    private ContentHostingService contentHostingService;
    @Setter
    private ServerConfigurationService serverConfigurationService;

    protected abstract LearningManagementSystem lms();
    protected abstract ScormResourceService resourceService();

    private final static String ZIP_MIMETYPE = "application/zip";

    private String collectionPackageRoot;
    private String launchPage;
    private boolean removeExistingContentPackage;
    private boolean hideContentPackageRoot;

    public void init() {
        collectionPackageRoot = serverConfigurationService.getString("tincanapi.package.root.dir", "TinCanAPI_Packages");
        launchPage = serverConfigurationService.getString("tincanapi.package.default.launch.page", "story.html");
        removeExistingContentPackage = serverConfigurationService.getBoolean("tincanapi.package.remove.existing.package.dir", true);
        hideContentPackageRoot = serverConfigurationService.getBoolean("tincanapi.package.hide.root.package.dir", true);
    }

    @Override
    public int validateAndProcess(InputStream inputStream, String packageName, String contentType) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null.");
        }
        if (StringUtils.isBlank(packageName)) {
            throw new IllegalArgumentException("Package name cannot be empty.");
        }
        if (StringUtils.isBlank(contentType)) {
            throw new IllegalArgumentException("Content type cannot be empty.");
        }
        if (StringUtils.equals(ZIP_MIMETYPE, contentType)) {
            return VALIDATION_WRONGMIMETYPE;
        }
        String launchUrl = processPackage(inputStream, packageName, contentType);

        return VALIDATION_SUCCESS;
    }

    @Override
    public TinCanAPIManifest createManifest() {
        // TODO implement this
        TinCanAPIManifest tinCanAPIManifest = new TinCanAPIManifest();

        return tinCanAPIManifest;
    }

    @Override
    public String processPackage(InputStream inputStream, String packageName, String contentType) {
        String currentContext = lms().currentContext();
        String siteRootCollection = ContentHostingService.COLLECTION_SITE + currentContext + "/";
        String rootPackageCollectionPath =  siteRootCollection + collectionPackageRoot + "/";
        String contentPackageCollectionId = packageName.substring(0, packageName.lastIndexOf("."));
        String contentPackageCollectionPath = rootPackageCollectionPath + contentPackageCollectionId + "/";
        String contentPackageZipFilePath = rootPackageCollectionPath + packageName;
        ContentCollectionEdit rootPackageCollectionEdit = null;
        ContentCollectionEdit contentPackageCollectionEdit = null;

        try {
            // check if root package dir exists, if not, create it
            List<String> siteRootChildren = contentHostingService.getCollection(siteRootCollection).getMembers();
            if (!siteRootChildren.contains(rootPackageCollectionPath)) {
                // root collection not found, create it
                rootPackageCollectionEdit = addOrEditCollection(rootPackageCollectionPath);
                rootPackageCollectionEdit.setHidden();
                addProperty(rootPackageCollectionEdit, ResourceProperties.PROP_DISPLAY_NAME, collectionPackageRoot);
                addProperty(rootPackageCollectionEdit, ResourceProperties.PROP_HIDDEN_WITH_ACCESSIBLE_CONTENT, Boolean.toString(hideContentPackageRoot));
                contentHostingService.commitCollection(rootPackageCollectionEdit);
            }

            // check if content package dir exists, if so, remove it (if configured)
            if (removeExistingContentPackage) {
                List<String> packageRootChildren = contentHostingService.getCollection(rootPackageCollectionPath).getMembers();
                if (packageRootChildren.contains(contentPackageCollectionPath)) {
                    // delete existing package collection
                    contentPackageCollectionEdit = contentHostingService.editCollection(contentPackageCollectionPath);
                    contentHostingService.removeCollection(contentPackageCollectionEdit);
                }
            }

            // add the zip file to the root content package dir
            ContentResourceEdit zipFile = addOrEditResource(contentPackageZipFilePath);
            zipFile.setContent(inputStream);
            zipFile.setContentType(ZIP_MIMETYPE);
            contentHostingService.commitResource(zipFile);

            // extract the files to the current directory
            contentHostingService.expandZippedResource(zipFile.getId());

            // remove the content package zip file after it's extracted
            zipFile = editResource(contentPackageZipFilePath);
            contentHostingService.removeResource(zipFile);
        } catch (Exception e) {
            log.error("An error occurred extracting the TinCanAPI zip file into resources.", e);
            return null;
        }

        // get the relative URL for launching the content
        String relativeUrl = contentPackageCollectionId + launchPage;

        return relativeUrl;
    }

    private ContentCollectionEdit addOrEditCollection(String collectionPath) {
        try {
            return addCollection(collectionPath);
        } catch (IdUsedException iue) {
            // id exists, edit the collection instead
            return editCollection(collectionPath);
        }
    }

    private ContentCollectionEdit addCollection(String collectionPath) throws IdUsedException {
        try {
            return contentHostingService.addCollection(collectionPath);
        } catch (IdInvalidException e) {
            e.printStackTrace();
        } catch (PermissionException e) {
            e.printStackTrace();
        } catch (InconsistentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ContentCollectionEdit editCollection(String collectionPath) {
        try {
            return contentHostingService.editCollection(collectionPath);
        } catch (IdUnusedException e) {
            e.printStackTrace();
        } catch (TypeException e) {
            e.printStackTrace();
        } catch (PermissionException e) {
            e.printStackTrace();
        } catch (InUseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ContentResourceEdit addOrEditResource(String resourcePath) {
        try {
            return addResource(resourcePath);
        } catch (IdUsedException e) {
            return editResource(resourcePath);
        }
    }

    private ContentResourceEdit addResource(String resourcePath) throws IdUsedException {
        try {
            return contentHostingService.addResource(resourcePath);
        } catch (PermissionException e) {
            e.printStackTrace();
        } catch (IdInvalidException e) {
            e.printStackTrace();
        } catch (InconsistentException e) {
            e.printStackTrace();
        } catch (ServerOverloadException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ContentResourceEdit editResource(String resourcePath) {
        try {
            return contentHostingService.editResource(resourcePath);
        } catch (PermissionException e) {
            e.printStackTrace();
        } catch (IdUnusedException e) {
            e.printStackTrace();
        } catch (TypeException e) {
            e.printStackTrace();
        } catch (InUseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void addProperty(Edit editEntity, String key, String value) {
        ResourcePropertiesEdit props = editEntity.getPropertiesEdit();
        props.addProperty(key, value);
    }

}
