package org.sakaiproject.articulate.tincan.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.api.ArticulateTCImporterService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMeta;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.articulate.tincan.util.ArticulateTCContentEntityUtils;
import org.sakaiproject.articulate.tincan.util.ArticulateTCDocumentUtils;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.event.api.EventTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCImporterServiceImpl implements ArticulateTCImporterService, ArticulateTCConstants {

    private final Logger log = LoggerFactory.getLogger(ArticulateTCImporterServiceImpl.class);

    @Setter
    private ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @Setter
    private ContentHostingService contentHostingService;

    @Setter
    private DeveloperHelperService developerHelperService;

    @Setter
    private EventTrackingService eventTrackingService;

    private String packageName;
    private String zipFileId;
    private String launchPageUrl;
    private String tincanXmlUrl;
    private String metaXmlUrl;
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
            log.error("Uploaded file is not of type {}", ARCHIVE_ZIP_MIMETYPE);
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
        metaXmlDocument = ArticulateTCDocumentUtils.parseResourceAsDocument(metaXmlUrl);

        // TinCanAPI content package
        boolean contentPackageCreatedSuccessfully = createContentPackage();
        if (!contentPackageCreatedSuccessfully) {
            return VALIDATION_CONVERTFAILED;
        }

        // move the temp file directory to the permanent directory
        moveContentPackage();

        StringBuilder eventRef = new StringBuilder("articulate/tc/site/")
            .append(getCurrentContext())
            .append("/user/")
            .append(getCurrentUserId())
            .append("/packageName/")
            .append(this.packageName);
        eventTrackingService.post(eventTrackingService.newEvent(SAKAI_EVENT_ADD, eventRef.toString() , true));

        return VALIDATION_SUCCESS;
    }

    @Override
    public boolean processPackage(InputStream inputStream, String packageName, String contentType) {
        try {
            // store the zip file
            storeZipFile(inputStream);

            // extract the files to the current directory
            contentHostingService.expandZippedResource(zipFileId);

            // remove the content package zip file after it's extracted
            removeZipFile();

            // find the launnch p[age URL, usually story.html
            findLaunchPageUrl();

            // find the meta.xml file
            findMetaXmlUrl();

            // find the tincan.xml file
            findTincanXmlUrl();

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
            log.error("An error occurred extracting the TinCanAPI zip file {} into resources.", packageName, e);
            return false;
        }

        return true;
    }

    @Override
    public boolean createContentPackage() {
        articulateTCContentPackage = new ArticulateTCContentPackage(processMetaXml(), launchPageUrl);

        if (!articulateTCContentPackage.isValid()) {
            log.error("The Articulate TinCanAPI Content Package is invalid. It has been deleted. \n {}", articulateTCContentPackage.toString());
            removeArchiveCollection();

            return false;
        }

        // set the title
        articulateTCContentPackage.setTitle(getDuplicateTitle());

        // save the content package to the database
        articulateTCContentPackageDao.save(articulateTCContentPackage);

        return true;
    }

    @Override
    public ArticulateTCMeta processMetaXml() {
        ArticulateTCMeta tinCanAPIMeta = null;

        try {
            NodeList projectList = metaXmlDocument.getElementsByTagName(XmlKeys.project.toString());
            Node project = projectList.item(0);
            Element element = (Element) project;

            tinCanAPIMeta = new ArticulateTCMeta(
                getCurrentContext(),
                getCurrentUserId(),
                element.getAttribute(XmlKeys.id.toString()),
                element.getAttribute(XmlKeys.title.toString())
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
            contentHostingService.getReference(launchPageUrl);
            contentHostingService.getResource(metaXmlUrl);
            contentHostingService.getResource(tincanXmlUrl);
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
            ContentCollectionEdit articulateCollectionEdit = ArticulateTCContentEntityUtils.addOrEditCollection(ARCHIVE_DEFAULT_STORAGE_PATH_PREFIX);

            return processDirectory(articulateCollectionEdit, true);
    }

    /**
     * Check if site directory exists, if not, create it
     * If it exists, update the configured settings
     *
     * @return true, if the site directory is valid
     */
    private boolean processSiteDirectory() {
        ContentCollectionEdit siteCollectionEdit = ArticulateTCContentEntityUtils.addOrEditCollection(getSiteCollectionRootPath());

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
        } catch (Exception e) {
            log.error("Error occurred processing the directory.", e);
            return false;
        } finally {
            developerHelperService.restoreCurrentUser();
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
            ContentResourceEdit zipFile = ArticulateTCContentEntityUtils.addOrEditResource(getPackageArchiveFilePath(true));
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
     * @return true, if moved successfully
     */
    private boolean moveContentPackage() {
        String existingDirectory = getPackageCollectionPath(false);

        try {
            List<String> packageCollectionRootChildren = contentHostingService.getCollection(getSiteCollectionRootPath()).getMembers();

            if (packageCollectionRootChildren.contains(existingDirectory)) {
                contentHostingService.removeCollection(existingDirectory);
            }
        } catch (Exception e) {
            log.error("Error removing existing directory: {}", existingDirectory, e);
            return false;
        }

        ContentCollectionEdit newCollectionEdit = ArticulateTCContentEntityUtils.addOrEditCollection(getPackageCollectionPath(true));

        if (newCollectionEdit == null) {
            return false;
        }

        newCollectionEdit = (ContentCollectionEdit) ArticulateTCContentEntityUtils.addProperties(
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
        String title = articulateTCContentPackage.getTitle();
        int count = articulateTCContentPackageDao.countContentPackages(getCurrentContext(), title);

        if (count > 1) {
            title += new StringBuilder(" (")
                .append(count)
                .append(")")
                .toString();
        }

        return title;
    }

    /**
     * Sets the timestamp for the temp directory
     * e.g "-1234567890"
     */
    private void setTimestamp() {
        this.timestamp = ARCHIVE_FILE_SUFFIX_SEPARATOR + Long.toString((new Date()).getTime());
    }

    /**
     * Path: MY_CONTENT_PACKAGE (optional timestamp suffix: "-1234567890")
     * 
     * @param includeTimestamp should the timestamp be included in the name?
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
        return new StringBuilder(ARCHIVE_DEFAULT_STORAGE_PATH_PREFIX)
            .append(getCurrentContext())
            .append(Entity.SEPARATOR)
            .toString();
    }

    /**
     * Path: /private/articulate/SITE_ID/MY_CONTENT_PACKAGE{-1234567890}/
     * 
     * @param includeTimestamp should the timestamp be included in the name?
     */
    private String getPackageCollectionPath(boolean includeTimestamp) {
        return new StringBuilder(getSiteCollectionRootPath())
            .append(getPackageCollectionId(includeTimestamp))
            .append(Entity.SEPARATOR)
            .toString();
    }

    /**
     * Path: /private/articulate/SITE_ID/MY_CONTENT_PACKAGE{-1234567890}.zip
     * 
     * @param includeTimestamp should the timestamp be included in the name?
     */
    private String getPackageArchiveFilePath(boolean includeTimestamp) {
        return new StringBuilder(getSiteCollectionRootPath())
            .append(getPackageCollectionId(includeTimestamp))
            .append(ARCHIVE_ZIP_EXTENSION)
            .toString();
    }

    /**
     * Attempts to find a launch page HTML file URL
     * Since the user can choose a name other than "story.html"
     */
    private void findLaunchPageUrl() {
        // get a listing of the files in the directory
        List<ContentResource> contentResources = getFilesInPackageDirectory();

        // no files in directory (this shouldn't happen...)
        if (CollectionUtils.isEmpty(contentResources)) {
            throw new IllegalStateException("Error:: no HTML file found for launch page.");
        }

        // search the directory for HTML files
        for (ContentResource contentResource : contentResources) {
            if (StringUtils.endsWithIgnoreCase(contentResource.getId(), ARCHIVE_DEFAULT_LAUNCH_PAGE)) {
                launchPageUrl = ARCHIVE_DEFAULT_URL_PATH_PREFIX + contentResource.getId();
                return;
            }

            if (StringUtils.endsWithIgnoreCase(contentResource.getId(), ARCHIVE_DEFAULT_LAUNCH_PAGE_HTML5_SUFFIX)) {
                String path = StringUtils.remove(contentResource.getId(), ARCHIVE_DEFAULT_LAUNCH_PAGE_HTML5_SUFFIX);
                launchPageUrl = ARCHIVE_DEFAULT_URL_PATH_PREFIX + path;
                return;
            }

            if (StringUtils.endsWithIgnoreCase(contentResource.getId(), ARCHIVE_DEFAULT_LAUNCH_PAGE_UNSUPPORTED_SUFFIX)) {
                String path = StringUtils.remove(contentResource.getId(), ARCHIVE_DEFAULT_LAUNCH_PAGE_UNSUPPORTED_SUFFIX);
                launchPageUrl = ARCHIVE_DEFAULT_URL_PATH_PREFIX + path;
                return;
            }
        }

        log.error("Error:: no HTML file found for launch page.");
    }

    /**
     * Attempt to find the meta.xml file in the directory
     */
    private void findMetaXmlUrl() {
        List<ContentResource> contentResources = getFilesInPackageDirectory();

        for (ContentResource contentResource : contentResources) {
            if (StringUtils.endsWithIgnoreCase(contentResource.getId(), ARCHIVE_META_XML_FILE)) {
                metaXmlUrl = contentResource.getId();
                return;
            }
        }

        log.error("Error:: no meta.xml file found for package.");
    }

    /**
     * Attempt to find the tincan.xml file in the directory
     */
    private void findTincanXmlUrl() {
        List<ContentResource> contentResources = getFilesInPackageDirectory();

        for (ContentResource contentResource : contentResources) {
            if (StringUtils.endsWithIgnoreCase(contentResource.getId(), ARCHIVE_TINCAN_XML_FILE)) {
                tincanXmlUrl = contentResource.getId();
                return;
            }
        }

        log.error("Error:: no tincan.xml file found for package.");
    }

    /**
     * Retrieve a list of all files under the package directory
     * 
     * @return a list of the {@link ContentResource} objects
     */
    private List<ContentResource> getFilesInPackageDirectory() {
        // get a listing of the files in the directory
        List<ContentResource> contentResources = contentHostingService.getAllResources(getPackageCollectionPath(true));

        // no files in directory (this shouldn't happen...)
        if (CollectionUtils.isEmpty(contentResources)) {
            log.error("Error:: no files found in package directory.");
            return new ArrayList<>();
        }

        return contentResources;
    }

}
