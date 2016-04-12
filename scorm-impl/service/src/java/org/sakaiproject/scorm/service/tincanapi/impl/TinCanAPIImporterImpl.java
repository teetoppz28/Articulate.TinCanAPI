package org.sakaiproject.scorm.service.tincanapi.impl;

import java.io.InputStream;
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
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIConstants;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIContentPackage;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIMeta;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.sakaiproject.scorm.service.tincanapi.api.TinCanAPIImporter;
import org.sakaiproject.scorm.service.tincanapi.impl.util.TinCanAPIContentEntityUtils;
import org.sakaiproject.scorm.service.tincanapi.impl.util.TinCanAPIDocumentUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class TinCanAPIImporterImpl implements TinCanAPIImporter, TinCanAPIConstants {

    private Log log = LogFactory.getLog(TinCanAPIImporterImpl.class);

    @Setter
    private ContentHostingService contentHostingService;
    @Setter
    private ServerConfigurationService serverConfigurationService;
    @Setter
    private DeveloperHelperService developerHelperService;
    @Setter
    private TinCanAPIDocumentUtils tinCanAPIDocumentUtils;
    @Setter
    private TinCanAPIContentEntityUtils tinCanAPIContentEntityUtils;

    protected abstract ContentPackageDao contentPackageDao();
    protected abstract ScormResourceService resourceService();

    /*
     * Default settings
     */
    private static String DEFAULT_PACKAGE_ROOT_NAME = "TinCanAPI_Packages";
    private static String DEFAULT_LAUNCH_PAGE = "story.html";
    private static boolean DEFAULT_HIDE_ROOT_DIRECTORY = true;
    private final static String DEFAULT_PATH_PREFIX = "/access/content";

    private String launchPage;
    private boolean hideContentPackageRoot;
    private String packageName;
    private String packageCollectionRootName;
    private String zipFileId;
    private Document metaXmlDocument;
    private TinCanAPIContentPackage tinCanAPIContentPackage;
    private String timestamp;

    public void init() {
        packageCollectionRootName = serverConfigurationService.getString("tincanapi.package.root.dir", DEFAULT_PACKAGE_ROOT_NAME);
        launchPage = serverConfigurationService.getString("tincanapi.package.default.launch.page", DEFAULT_LAUNCH_PAGE);
        hideContentPackageRoot = serverConfigurationService.getBoolean("tincanapi.package.hide.root.package.dir", DEFAULT_HIDE_ROOT_DIRECTORY);
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
        setTimestamp();

        // extract archive, save to resources, get the launch URL for content, delete archive file
        boolean packageProcessedSuccessfully = processPackage(inputStream, packageName, contentType);
        if (!packageProcessedSuccessfully) {
            return VALIDATION_CONVERTFAILED;
        }

        // parse meta.xml for content package data
        metaXmlDocument = tinCanAPIDocumentUtils.parseResourceAsDocument(getPackageCollectionPath(true) + META_XML_FILE);

        // TinCanAPI content package
        boolean contentPackageCreatedSuccessfully = createContentPackage();
        if (!contentPackageCreatedSuccessfully) {
            return VALIDATION_CONVERTFAILED;
        }

        // move the temp file directory to the permanent directory (if configured)
        moveContentPackage();

        return VALIDATION_SUCCESS;
    }

    @Override
    public boolean processPackage(InputStream inputStream, String packageName, String contentType) {
        try {
            storeZipFile(inputStream);

            // extract the files to the current directory
            contentHostingService.expandZippedResource(zipFileId);

            // remove the content package zip file after it's extracted
            removeZipFile();

            boolean validated = validateTinCanAPIArchive();
            if (!validated) {
                // not a valid TinCanAPI archive package, remove it
                removeArchiveCollection();
                return false;
            }

            boolean rootValid = processRootDirectory();
            if (!rootValid) {
                // not a valid root archive directory, remove the expanded archive package
                removeArchiveCollection();
                return false;
            }

            // get the relative URL for launching the content
            getLaunchUrl(true);
        } catch (Exception e) {
            log.error("An error occurred extracting the TinCanAPI zip file into resources.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean createContentPackage() {
        tinCanAPIContentPackage = new TinCanAPIContentPackage(processMetaXml(), getLaunchUrl(true));

        if (!tinCanAPIContentPackage.isValid()) {
            log.error("The TinCanAPI Content Package is invalid. It has been deleted. \n" + tinCanAPIContentPackage.toString());
            removeArchiveCollection();

            return false;
        }

        // save the content package to the database
        contentPackageDao().save(tinCanAPIContentPackage.getContentPackage());

        return true;
    }

    @Override
    public TinCanAPIMeta processMetaXml() {
        TinCanAPIMeta tinCanAPIMeta = null;

        try {
            NodeList projectList = metaXmlDocument.getElementsByTagName("project");
            Node project = projectList.item(0);
            Element element = (Element) project;

            tinCanAPIMeta = new TinCanAPIMeta(
                getCurrentContext(),
                getCurrentUserId(),
                element.getAttribute(TinCanAPIMeta.ATTR_ID),
                element.getAttribute(TinCanAPIMeta.ATTR_TITLE)
            );
        } catch (Exception e) {
            log.error("Error occurred processing the meta XML document.", e);
            removeArchiveCollection();
        }

        return tinCanAPIMeta;
    }

    /**
     * Validates the zip archive uploaded is a TinCanAPI package:
     * 1. has the launch page file (default: story.html)
     * 2. has the meta.xml file
     * 3. has the tincan.xml file
     * 
     * @return true, if all the files above exist (content not verified)
     */
    private boolean validateTinCanAPIArchive() {
        try {
            contentHostingService.getResource(getPackageCollectionPath(true) + TINCAN_XML_FILE);
            contentHostingService.getResource(getPackageCollectionPath(true) + META_XML_FILE);
            contentHostingService.getResource(getPackageCollectionPath(true) + launchPage);
        } catch (Exception e) {
            log.error("Error validating archive.", e);
            return false;
        }
  
        return true;
    }

    /**
     * Check if root package directory exists, if not, create it
     * If it exists, update the configured settings
     *
     * @return true, if the root directory is valid
     */
    private boolean processRootDirectory() {
        try {
            ContentCollectionEdit rootPackageCollectionEdit = tinCanAPIContentEntityUtils.addOrEditCollection(getPackageCollectionRootPath());
            rootPackageCollectionEdit = (ContentCollectionEdit) tinCanAPIContentEntityUtils.addProperties(
                rootPackageCollectionEdit,
                new String[] {ResourceProperties.PROP_DISPLAY_NAME, ResourceProperties.PROP_HIDDEN_WITH_ACCESSIBLE_CONTENT},
                new String[] {packageCollectionRootName, Boolean.toString(hideContentPackageRoot)}
            );

            contentHostingService.commitCollection(rootPackageCollectionEdit);
        } catch (Exception e) {
            log.error("Error occurred processing the root directory.", e);
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
            ContentResourceEdit zipFile = tinCanAPIContentEntityUtils.addOrEditResource(getPackageArchiveFilePath(true));
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
            contentHostingService.removeResource(getPackageArchiveFilePath(true));
        } catch (Exception e) {
            log.error("Error removing archive file", e);
        }
    }

    /**
     * Removes the expanded archive directory from resources
     */
    private void removeArchiveCollection() {
        try {
            contentHostingService.removeCollection(getPackageCollectionPath(true));
        } catch (Exception e) {
            log.error("Error removing archive file", e);
        }
    }

    /**
     * Move the new package content to the permanent directory (if configured)
     * @return
     */
    private boolean moveContentPackage() {
        String existingDirectory = getPackageCollectionPath(false);

        try {
            List<String> packageCollectionRootChildren = contentHostingService.getCollection(getPackageCollectionRootPath()).getMembers();
            if (packageCollectionRootChildren.contains(existingDirectory)) {
                contentHostingService.removeCollection(existingDirectory);
            }
        } catch (Exception e) {
            log.error("Error removing existing directory: " + existingDirectory, e);
            return false;
        }

        ContentCollectionEdit newCollectionEdit = tinCanAPIContentEntityUtils.addOrEditCollection(getPackageCollectionPath(true));
        newCollectionEdit = (ContentCollectionEdit) tinCanAPIContentEntityUtils.addProperties(
            newCollectionEdit,
            new String[] {ResourceProperties.PROP_DISPLAY_NAME},
            new String[] {getPackageCollectionId(false)}
        );
        contentHostingService.commitCollection(newCollectionEdit);

        // get the latest URL for the content launch page
        getLaunchUrl(false);

        return true;
    }

    /**
     * Sets the timestamp for the temp directory
     * e.g "-1234567890"
     */
    private void setTimestamp() {
        this.timestamp = "-" + Long.toString((new Date()).getTime());
    }

    /**
     * Path: MY_CONTENT_PACKAGE (optional timestamp suffix: "-1234567890")
     */
    private String getPackageCollectionId(boolean includeTimestamp) {
        String suffix = includeTimestamp ? timestamp : "";

        return packageName.substring(0, packageName.lastIndexOf(".")) + suffix;
    }

    /**
     * Path: SITE_ID
     */
    private String getCurrentContext() {
        return developerHelperService.getCurrentLocationId();
    }

    /**
     * Get the current user's internal USER_ID (not EID)
     */
    private String getCurrentUserId() {
        return developerHelperService.getCurrentUserId();
    }

    /**
     * Path: /group/SITE_ID/
     */
    private String getSiteCollectionRootPath() {
        return ContentHostingService.COLLECTION_SITE + getCurrentContext() + Entity.SEPARATOR;
    }

    /**
     * Path: /group/SITE_ID/TinCanAPI_Packages/
     */
    private String getPackageCollectionRootPath() {
        return getSiteCollectionRootPath() + packageCollectionRootName + Entity.SEPARATOR;
    }

    /**
     * Path: /group/SITE_ID/TinCanAPI_Packages/MY_CONTENT_PACKAGE-1234567890
     */
    private String getPackageCollectionPath(boolean includeTimestamp) {
        return getPackageCollectionRootPath() + getPackageCollectionId(includeTimestamp) + Entity.SEPARATOR;
    }

    /**
     * Path: /group/SITE_ID/TinCanAPI_Packages/MY_CONTENT_PACKAGE-1234567890.zip
     */
    private String getPackageArchiveFilePath(boolean includeTimestamp) {
        return getPackageCollectionRootPath() + getPackageCollectionId(includeTimestamp) + ".zip";
    }

    /**
     * Path: /access/content/group/SITE_ID/TinCanAPI_Packages/MY_CONTENT_PACKAGE-1234567890/story.html
     */
    private String getLaunchUrl(boolean includeTimestamp) {
        return DEFAULT_PATH_PREFIX + getPackageCollectionPath(includeTimestamp) + launchPage;
    }

}
