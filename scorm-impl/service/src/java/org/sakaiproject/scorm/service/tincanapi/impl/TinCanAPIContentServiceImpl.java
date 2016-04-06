package org.sakaiproject.scorm.service.tincanapi.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.scorm.api.ScormConstants;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIManifest;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.sakaiproject.scorm.service.tincanapi.api.TinCanAPIContentService;

public abstract class TinCanAPIContentServiceImpl implements TinCanAPIContentService, ScormConstants {

    @Setter
    private ContentHostingService contentHostingService;

    protected abstract LearningManagementSystem lms();
    protected abstract ScormResourceService resourceService();

    private final static String COLLECTION_PACKAGE_ROOT = "TinCanAPI_Packages";
    private final static String PROPERTY_ALLOW_INLINE = "SAKAI:allow_inline";
    private final static String LAUNCH_PAGE = "story.html";

    @Override
    public int validateAndProcess(InputStream inputStream, String packageName, String contentType) {
        // TODO check content type is "application/zip"
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
        String rootPackageCollectionPath = ContentHostingService.COLLECTION_SITE + currentContext + "/" + COLLECTION_PACKAGE_ROOT + "/";
        String contentPackageCollectionId = packageName.substring(0, packageName.lastIndexOf(".")) + "/";
        String contentPackageCollectionPath = rootPackageCollectionPath + contentPackageCollectionId;
        String contentPackageZipFilePath = rootPackageCollectionPath + packageName;
        ContentCollectionEdit rootPackageCollection = null;
        ContentCollectionEdit contentPackageCollection = null;

        try {
            // check if root dir exists
            try {
                contentHostingService.getCollectionSize(rootPackageCollectionPath);
            } catch (IdUnusedException iue) {
                // root collection not found, create it
                rootPackageCollection = contentHostingService.addCollection(rootPackageCollectionPath);
                contentHostingService.commitCollection(rootPackageCollection);
            }

            try {
                contentHostingService.removeCollection(contentPackageCollectionPath);
            } catch (IdUnusedException iue) {
                // swallowed, this is fine since the package dir doesn't exist
            }

            // add the zip file to the root content package dir
            ContentResourceEdit zipFile = contentHostingService.addResource(contentPackageZipFilePath);
            zipFile.setContent(inputStream);
            zipFile.setContentType("application/zip");
            contentHostingService.commitResource(zipFile);

            // extract the files to the current directory
            contentHostingService.expandZippedResource(zipFile.getId());

            // remove the content package zip file after it's unzipped
            contentHostingService.removeResource(zipFile);

            // handle post-extraction necessities
            contentPackageCollection = contentHostingService.addCollection(contentPackageCollectionPath);

            // set the appropriate properties
            ResourcePropertiesEdit props = contentPackageCollection.getPropertiesEdit();
            props.addProperty(PROPERTY_ALLOW_INLINE, "true");
            List<String> children = contentPackageCollection.getMembers();

            for (int j = 0; j < children.size(); j++) {
                String resId = children.get(j);
                if (resId.endsWith("/")) {
                    setPropertyOnFolderRecursively(resId, PROPERTY_ALLOW_INLINE, "true");
                }
            }

            contentHostingService.commitCollection(contentPackageCollection);
        } catch (Exception e) {
            // general error catching
            // TODO expand this
            e.printStackTrace();
            return null;
        }

        // get the relative URL for launching the content
        String relativeUrl = contentPackageCollectionId + LAUNCH_PAGE;

        return relativeUrl;
    }

    /**
     * Sets the given property on the given resource
     * 
     * @param resourceId
     * @param property
     * @param value
     */
    private void setPropertyOnFolderRecursively(String resourceId, String property, String value) {
        try {
            if (contentHostingService.isCollection(resourceId)) {
                // collection
                ContentCollectionEdit col = contentHostingService.editCollection(resourceId);

                ResourcePropertiesEdit resourceProperties = col.getPropertiesEdit();
                resourceProperties.addProperty(property, Boolean.valueOf(value).toString());
                contentHostingService.commitCollection(col);

                List<String> children = col.getMembers();
                for (int i = 0; i < children.size(); i++) {
                    String resId = children.get(i);
                    if (resId.endsWith("/")) {
                        setPropertyOnFolderRecursively(resId, property, value);
                    }
                }

            } else {
                // resource
                ContentResourceEdit res = contentHostingService.editResource(resourceId);
                ResourcePropertiesEdit resourceProperties = res.getPropertiesEdit();
                resourceProperties.addProperty(property, Boolean.valueOf(value).toString());
                contentHostingService.commitResource(res, NotificationService.NOTI_NONE);
            }
        } catch (Exception pe) {
            pe.printStackTrace();
        }
    }

}
