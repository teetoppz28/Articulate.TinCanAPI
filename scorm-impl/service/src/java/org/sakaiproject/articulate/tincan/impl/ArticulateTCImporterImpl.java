package org.sakaiproject.articulate.tincan.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.api.ArticulateTCImporter;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageSettingsDao;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCContentPackage;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMeta;
import org.sakaiproject.articulate.tincan.util.ArticulateTCContentEntityUtils;
import org.sakaiproject.articulate.tincan.util.ArticulateTCDocumentUtils;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ArticulateTCImporterImpl implements ArticulateTCImporter, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCImporterImpl.class);

    @Setter
    private ArticulateTCContentEntityUtils articulateTCContentEntityUtils;

    @Setter
    private ArticulateTCContentPackageSettingsDao articulateTCContentPackageSettingsDao;

    @Setter
    private ArticulateTCDocumentUtils articulateTCDocumentUtils;

    @Setter
    private ContentHostingService contentHostingService;

    @Setter
    private DeveloperHelperService developerHelperService;

    protected abstract ContentPackageDao contentPackageDao();
    protected abstract ScormResourceService resourceService();

    private String packageName;
    private String zipFileId;
    private Document metaXmlDocument;
    private ArticulateTCContentPackage articulateTCContentPackage;
    private String timestamp;

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
        if (!StringUtils.equals(ARCHIVE_ZIP_MIMETYPE, contentType)) {
            log.error("Uploaded file is not of type " + ARCHIVE_ZIP_MIMETYPE);
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
        metaXmlDocument = articulateTCDocumentUtils.parseResourceAsDocument(getPackageCollectionPath(true) + ARCHIVE_META_XML_FILE);

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

            boolean validated = validateArticulateTinCanAPIArchive();
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

            boolean siteValid = processSiteDirectory();
            if (!siteValid) {
                // not a valid site archive directory, remove the expanded archive package
                removeArchiveCollection();
                return false;
            }
        } catch (Exception e) {
            log.error("An error occurred extracting the TinCanAPI zip file into resources.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean createContentPackage() {
        articulateTCContentPackage = new ArticulateTCContentPackage(processMetaXml(), getLaunchUrl(true));

        if (!articulateTCContentPackage.isValid()) {
            log.error("The Articulate TinCanAPI Content Package is invalid. It has been deleted. \n" + articulateTCContentPackage.toString());
            removeArchiveCollection();

            return false;
        }

        // set the title
        articulateTCContentPackage.getContentPackage().setTitle(getDuplicateTitle());

        // save the content package to the database
        contentPackageDao().save(articulateTCContentPackage.getContentPackage());

        // synchronize any content package and articulate package settings
        articulateTCContentPackage.synchronizePackageSettings();

        // save the Articulate package settings to the database
        articulateTCContentPackageSettingsDao.save(articulateTCContentPackage.getArticulateTCContentPackageSettings());

        return true;
    }

    @Override
    public ArticulateTCMeta processMetaXml() {
        ArticulateTCMeta tinCanAPIMeta = null;

        try {
            NodeList projectList = metaXmlDocument.getElementsByTagName("project");
            Node project = projectList.item(0);
            Element element = (Element) project;

            tinCanAPIMeta = new ArticulateTCMeta(
                getCurrentContext(),
                getCurrentUserId(),
                element.getAttribute(ArticulateTCMeta.ATTR_ID),
                element.getAttribute(ArticulateTCMeta.ATTR_TITLE)
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
    private boolean validateArticulateTinCanAPIArchive() {
        try {
            contentHostingService.getResource(getPackageCollectionPath(true) + ARCHIVE_TINCAN_XML_FILE);
            contentHostingService.getResource(getPackageCollectionPath(true) + ARCHIVE_META_XML_FILE);
            contentHostingService.getResource(getPackageCollectionPath(true) + ARCHIVE_DEFAULT_LAUNCH_PAGE);
        } catch (Exception e) {
            log.error("Error validating archive.", e);
            return false;
        }
  
        return true;
    }

    /**
     * Check if root articulate directory exists, if not, create it
     * If it exists, update the configured settings
     *
     * @return true, if the root articulate directory is valid
     */
    private boolean processRootDirectory() {
            ContentCollectionEdit articulateCollectionEdit = articulateTCContentEntityUtils.addOrEditCollection(ARCHIVE_DEFAULT_STORAGE_PATH_PREFIX);

            return processDirectory(articulateCollectionEdit, true);
    }

    /**
     * Check if site directory exists, if not, create it
     * If it exists, update the configured settings
     *
     * @return true, if the site directory is valid
     */
    private boolean processSiteDirectory() {
        ContentCollectionEdit siteCollectionEdit = articulateTCContentEntityUtils.addOrEditCollection(getSiteCollectionRootPath());

        return processDirectory(siteCollectionEdit, false);
    }

    /**
     * Creates or updates the given collection edit object
     * 
     * @param contentCollectionEdit the {@link ContentCollectionEdit} object to modify
     * @return true, if successfully modified
     */
    private boolean processDirectory(ContentCollectionEdit contentCollectionEdit, boolean isPublicAccess) {
        if (contentCollectionEdit == null) {
            return false;
        }

        try {
            developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);

            if (isPublicAccess) {
                contentCollectionEdit.setPublicAccess();
            }

            contentHostingService.commitCollection(contentCollectionEdit);

            developerHelperService.restoreCurrentUser();
        } catch (Exception e) {
            log.error("Error occurred processing the directory.", e);
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
            ContentResourceEdit zipFile = articulateTCContentEntityUtils.addOrEditResource(getPackageArchiveFilePath(true));
            zipFile.setContent(inputStream);
            zipFile.setContentType(ARCHIVE_ZIP_MIMETYPE);
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
     * Move the new package content to the permanent directory
     * 
     * @return
     */
    private boolean moveContentPackage() {
        String existingDirectory = getPackageCollectionPath(false);

        try {
            List<String> packageCollectionRootChildren = contentHostingService.getCollection(getSiteCollectionRootPath()).getMembers();
            if (packageCollectionRootChildren.contains(existingDirectory)) {
                contentHostingService.removeCollection(existingDirectory);
            }
        } catch (Exception e) {
            log.error("Error removing existing directory: " + existingDirectory, e);
            return false;
        }

        ContentCollectionEdit newCollectionEdit = articulateTCContentEntityUtils.addOrEditCollection(getPackageCollectionPath(true));

        if (newCollectionEdit == null) {
            return false;
        }

        newCollectionEdit = (ContentCollectionEdit) articulateTCContentEntityUtils.addProperties(
            newCollectionEdit,
            new String[] {ResourceProperties.PROP_DISPLAY_NAME},
            new String[] {getDuplicateTitle()}
        );
        contentHostingService.commitCollection(newCollectionEdit);

        return true;
    }

    /**
     * Checks to see if a package with this name already exists, if so, append a numerical suffix
     * 
     * @return the name of the package with any necessary suffix
     */
    private String getDuplicateTitle() {
        String title = articulateTCContentPackage.getContentPackage().getTitle();
        int count = contentPackageDao().countContentPackages(getCurrentContext(), title);

        // count return from method is 1, even if there are no rows with this title in the site
        if (count > 1) {
            title += " (" + --count + ")";
        }

        return title;
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
     * Path: /private/articulate/SITE_ID/
     */
    private String getSiteCollectionRootPath() {
        return ARCHIVE_DEFAULT_STORAGE_PATH_PREFIX + getCurrentContext() + Entity.SEPARATOR;
    }

    /**
     * Path: /private/articulate/SITE_ID/MY_CONTENT_PACKAGE-1234567890/
     */
    private String getPackageCollectionPath(boolean includeTimestamp) {
        return getSiteCollectionRootPath() + getPackageCollectionId(includeTimestamp) + Entity.SEPARATOR;
    }

    /**
     * Path: /private/articulate/SITE_ID/MY_CONTENT_PACKAGE-1234567890.zip
     */
    private String getPackageArchiveFilePath(boolean includeTimestamp) {
        return getSiteCollectionRootPath() + getPackageCollectionId(includeTimestamp) + ".zip";
    }

    /**
     * Path: /access/content/private/articulate/SITE_ID/MY_CONTENT_PACKAGE-1234567890/story.html
     */
    private String getLaunchUrl(boolean includeTimestamp) {
        return ARCHIVE_DEFAULT_URL_PATH_PREFIX + getPackageCollectionPath(includeTimestamp) + ARCHIVE_DEFAULT_LAUNCH_PAGE;
    }

}
