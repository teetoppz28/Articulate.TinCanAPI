package org.sakaiproject.scorm.service.tincanapi.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIConstants;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIContentPackage;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIMeta;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.sakaiproject.scorm.service.tincanapi.api.TinCanAPIContentService;
import org.sakaiproject.scorm.service.tincanapi.impl.util.TinCanAPIContentEntityUtils;
import org.sakaiproject.scorm.service.tincanapi.impl.util.TinCanAPIDocumentUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class TinCanAPIContentServiceImpl implements TinCanAPIContentService, TinCanAPIConstants {

    private Log log = LogFactory.getLog(TinCanAPIContentServiceImpl.class);

    @Setter
    private ContentHostingService contentHostingService;
    @Setter
    private ServerConfigurationService serverConfigurationService;
    @Setter
    private TinCanAPIDocumentUtils tinCanAPIDocumentUtils;
    @Setter
    private TinCanAPIContentEntityUtils tinCanAPIContentEntityUtils;

    protected abstract ContentPackageDao contentPackageDao();
    protected abstract LearningManagementSystem lms();
    protected abstract ScormResourceService resourceService();

    private String launchPage;
    private boolean removeExistingContentPackage;
    private boolean hideContentPackageRoot;
    private String packageName;
    private String packageCollectionRootName;
    private String packageCollectionId;
    private String zipFileId;
    private Document metaXmlDocument;
    private TinCanAPIMeta tinCanAPIMeta;
    private TinCanAPIContentPackage tinCanAPIContentPackage;
    private String launchUrl;
    private String timestamp;

    public void init() {
        packageCollectionRootName = serverConfigurationService.getString("tincanapi.package.root.dir", "TinCanAPI_Packages");
        launchPage = serverConfigurationService.getString("tincanapi.package.default.launch.page", "story.html");
        removeExistingContentPackage = serverConfigurationService.getBoolean("tincanapi.package.remove.existing.package.dir", true);
        hideContentPackageRoot = serverConfigurationService.getBoolean("tincanapi.package.hide.root.package.dir", true);

        tinCanAPIMeta = new TinCanAPIMeta();
        tinCanAPIContentPackage = new TinCanAPIContentPackage();
        timestamp = "-" + Long.toString((new Date()).getTime());
    }

    @Override
    public int validateAndProcess(InputStream inputStream, String packageName, String contentType) {
        if (inputStream == null) {
            log.error("InputStream cannot be null.");
            return VALIDATION_NOFILE;
        }
        if (StringUtils.isBlank(packageName)) {
            log.error("Package name cannot be empty.");
            return VALIDATION_NOTWELLFORMED;
        }
        if (!StringUtils.equals(ZIP_MIMETYPE, contentType)) {
            log.error("Uploaded file is not of type " + ZIP_MIMETYPE);
            return VALIDATION_WRONGMIMETYPE;
        }

        this.packageName = packageName;

        // save and validate the archive
        boolean uploadProcessedSuccessfully = processUpload(inputStream);
        if (!uploadProcessedSuccessfully) {
            log.error("Error processing the uploaded file");
            return VALIDATION_MISSINGREQUIREDFILES;
        }

        // extract archive, save to resources, get the launch URL for content, delete archive file
        boolean packageProcessedSuccessfully = processPackage(packageName, contentType);
        if (!packageProcessedSuccessfully) {
            removeZipFile();
            return VALIDATION_CONVERTFAILED;
        }

        // parse meta.xml for content package data
        metaXmlDocument = tinCanAPIDocumentUtils.parseResourceAsDocument(getPackageCollectionPath() + META_XML_FILE);

        // TinCanAPI content package
        boolean contentPackageCreatedSuccessfully = createContentPackage();
        if (!contentPackageCreatedSuccessfully) {
            return VALIDATION_CONVERTFAILED;
        }

        // save the content package to the database
        contentPackageDao().save(tinCanAPIContentPackage);

        // move the temp file directory to the permanent directory (if configured)
        moveContentPackage();

        return VALIDATION_SUCCESS;
    }

    @Override
    public boolean processUpload(InputStream inputStream) {
        try {
            storeZipFile(inputStream);
            boolean validated = validateTinCanAPIArchive();
            if (!validated) {
                // not a valid TinCanAPI archive package, remove it
                removeZipFile();
            }

            return validated;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean processPackage(String packageName, String contentType) {
        try {
            boolean rootValid = processRootDirectory();
            if (!rootValid) {
                return false;
            }

            // extract the files to the current directory
            contentHostingService.expandZippedResource(zipFileId);

            // remove the content package zip file after it's extracted
            removeZipFile();

            // get the relative URL for launching the content
            calculateLaunchUrl();
        } catch (Exception e) {
            log.error("An error occurred extracting the TinCanAPI zip file into resources.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean createContentPackage() {
        boolean validMetaXml = processMetaXml();
        if (!validMetaXml) {
            return false;
        }

        tinCanAPIContentPackage = new TinCanAPIContentPackage(tinCanAPIMeta, launchUrl);

        return tinCanAPIContentPackage.isValid();
    }

    @Override
    public boolean processMetaXml() {
        try {
            NodeList projectList = metaXmlDocument.getElementsByTagName("project");
            Node project = projectList.item(0);
            Element element = (Element) project;
    
            tinCanAPIMeta = new TinCanAPIMeta(
                element.getAttribute(TinCanAPIMeta.ATTR_COURSEID),
                element.getAttribute(TinCanAPIMeta.ATTR_ID),
                element.getAttribute(TinCanAPIMeta.ATTR_TITLE)
            );
        } catch (Exception e) {
            log.error("Error occurred processing the meta XML document.", e);
            return false;
        }

        return tinCanAPIMeta.isValid();
    }

    /**
     * Validates the zip archive uploaded is a TinCanAPI package:
     * 1. has the launch page file (default: story.html)
     * 2. has the meta.xml file
     * 3. has the tincan.xml file
     * 
     * @return true, if all the files above exist in the archive (content not verified)
     */
    private boolean validateTinCanAPIArchive() {
        Path zipFilePath = Paths.get(getPackageArchiveFilePath());
        try {
            FileSystem fs = FileSystems.newFileSystem(zipFilePath, null);
            Path tincanXmlPath = fs.getPath(TINCAN_XML_FILE);
            Path metaXmlPath = fs.getPath(META_XML_FILE);
            Path launchPagePath = fs.getPath(launchPage);

            return Files.exists(tincanXmlPath) && Files.exists(metaXmlPath) && Files.exists(launchPagePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if root package directory exists, if not, create it
     *
     * @return true, if the root directory is valid
     */
    private boolean processRootDirectory() {
        try {
            List<String> siteRootChildren = contentHostingService.getCollection(getSiteCollectionRootPath()).getMembers();
            if (!siteRootChildren.contains(getPackageCollectionRootPath())) {
                // root collection not found, create it
                ContentCollectionEdit rootPackageCollectionEdit = tinCanAPIContentEntityUtils.addOrEditCollection(getPackageCollectionRootPath());
                rootPackageCollectionEdit.setHidden();
                tinCanAPIContentEntityUtils.addProperty(rootPackageCollectionEdit, ResourceProperties.PROP_DISPLAY_NAME, packageCollectionRootName);
                tinCanAPIContentEntityUtils.addProperty(rootPackageCollectionEdit, ResourceProperties.PROP_HIDDEN_WITH_ACCESSIBLE_CONTENT, Boolean.toString(hideContentPackageRoot));
                contentHostingService.commitCollection(rootPackageCollectionEdit);
            }
        } catch (Exception e) {
            log.error("Error occurred processing the root directory");
            return false;
        }

        return true;
    }

    /**
     * Store the zip file in the root package directory
     *
     * @return true, if zip file stored successfully
     */
    private boolean storeZipFile(InputStream inputStream) {
        try {
            ContentResourceEdit zipFile = tinCanAPIContentEntityUtils.addOrEditResource(getPackageArchiveFilePath());
            zipFile.setContent(inputStream);
            zipFile.setContentType(ZIP_MIMETYPE);
            contentHostingService.commitResource(zipFile);
            zipFileId = zipFile.getId();
        } catch (Exception e) {
            log.error("Error storing zip file to resources.", e);
            return false;
        }

        return true;
    }

    /**
     * Removes the archive file from resources
     */
    private void removeZipFile() {
        try {
            contentHostingService.removeResource(getPackageArchiveFilePath());
        } catch (Exception e) {
            log.error("Error removing archive file", e);
        }
    }

    /**
     * Move the new package content to the permanent directory (if configured)
     * @return
     */
    private boolean moveContentPackage() {
        if (removeExistingContentPackage) {
            String existingDirectory = StringUtils.remove(getPackageCollectionPath(), timestamp);
            try {
                contentHostingService.removeCollection(existingDirectory);
            } catch (IdUnusedException e) {
                // swallowed, the directory doesn't exist, no worries
            } catch (Exception e) {
                log.error("Error removing existing directory: " + existingDirectory, e);
                return false;
            }

            packageCollectionId = StringUtils.remove(packageCollectionId, timestamp);
            ContentCollectionEdit newCollectionEdit = tinCanAPIContentEntityUtils.addOrEditCollection(getPackageCollectionPath());
            tinCanAPIContentEntityUtils.addProperty(newCollectionEdit, ResourceProperties.PROP_DISPLAY_NAME, packageCollectionId);
            contentHostingService.commitCollection(newCollectionEdit);

            // get the latest URL for the content launch page
            calculateLaunchUrl();
        }

        return true;
    }

    /**
     * Path: MY_CONTENT_PACKAGE-1234567890
     */
    private String getPackageCollectionId() {
        return packageName.substring(0, packageName.lastIndexOf(".")) + timestamp;
    }

    /**
     * Path: SITE_ID
     */
    private String getCurrentContext() {
        return lms().currentContext();
    }

    /**
     * Path: /group/SITE_ID/
     */
    private String getSiteCollectionRootPath() {
        return ContentHostingService.COLLECTION_SITE + getCurrentContext() + "/";
    }

    /**
     * Path: /group/SITE_ID/TinCanAPI_Packages/
     */
    private String getPackageCollectionRootPath() {
        return getSiteCollectionRootPath() + packageCollectionRootName + "/";
    }

    /**
     * Path: /group/SITE_ID/TinCanAPI_Packages/MY_CONTENT_PACKAGE-1234567890
     */
    private String getPackageCollectionPath() {
        return getPackageCollectionRootPath() + getPackageCollectionId() + "/";
    }

    /**
     * Path: /group/SITE_ID/TinCanAPI_Packages/MY_CONTENT_PACKAGE-1234567890.zip
     */
    private String getPackageArchiveFilePath() {
        return getPackageCollectionRootPath() + getPackageCollectionId() + ".zip";
    }

    /**
     * Path: /group/SITE_ID/TinCanAPI_Packages/MY_CONTENT_PACKAGE-1234567890/story.html
     */
    private void calculateLaunchUrl() {
        launchUrl = getPackageCollectionPath() + launchPage;
    }

}
