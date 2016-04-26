package org.sakaiproject.articulate.tincan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.site.api.SiteService;

public abstract class ArticulateTCEntityProviderServiceUtils implements ArticulateTCConstants {

    private static Log log = LogFactory.getLog(ArticulateTCEntityProviderServiceUtils.class);

    @Setter
    private DeveloperHelperService developerHelperService;
    @Setter
    private SiteService siteService;

    protected abstract ContentPackageDao contentPackageDao();

    /**
     * Decodes the URL-encoded string
     * 
     * @param str
     * @return
     */
    public String decodeString(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        try {
            return URLDecoder.decode(str, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error unencoding the string.", e);
        }

        return str;
    }

    /**
     * Gets the payload from the {@link HttpServletRequest} object
     * 
     * @param request
     * @return
     */
    public String getRequestPayload(HttpServletRequest request) {
        String content = "";

        try {
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            content = sb.toString();
        } catch (IOException e) {
            log.error("Error retrieving the request payload.", e);
        }

        return content;
    }

    /**
     * Gets the JSON string from the content variable in the payload
     * 
     * @param str the payload string from the request
     * @return
     */
    public String getContentDataFromPayload(String str) {        ArticulateTCRequestPayload articulateTCRequestPayload = getPayloadObject(str);

        if (StringUtils.isBlank(articulateTCRequestPayload.getContent())) {
            // should not get here... there must not be a "content" portion in the payload
            throw new IllegalArgumentException("Request payload does not contain a valid content statement string.");
        }

        return articulateTCRequestPayload.getContent();
    }

    /**
     * Retrieves the payload as an object
     * 
     * @param str
     * @return
     */
    public ArticulateTCRequestPayload getPayloadObject(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        String decodedPayload = decodeString(str);

        ArticulateTCRequestPayload articulateTCRequestPayload = new ArticulateTCRequestPayload(decodedPayload);
        articulateTCRequestPayload.setUserId(populateCurrentUser());
        articulateTCRequestPayload.setSiteId(populateCurrentSite(articulateTCRequestPayload.getPackageId()));

        return articulateTCRequestPayload;
    }

    /**
     * Gets a mapping of payload data
     * 
     * @param str the payload string from the request
     * @return
     */
    public ArticulateTCRequestPayload getStateDataFromPayload(String str) {
        ArticulateTCRequestPayload articulateTCRequestPayload = getPayloadObject(str);

        if (!articulateTCRequestPayload.isValid()) {
            // should not get here... there must not be a "content" portion in the payload
            throw new IllegalArgumentException("Request payload does not contain a valid activity state string.");
        }

        return articulateTCRequestPayload;
    }

    private String populateCurrentUser() {
        String userId = developerHelperService.getCurrentUserId();

        if (StringUtils.isBlank(userId)) {
            throw new SecurityException("Error: no current user is defined.");
        }

        return userId;
    }

    private String populateCurrentSite(String packageId) {
        ContentPackage contentPackage = contentPackageDao().load(Long.parseLong(packageId));

        if (contentPackage == null) {
            throw new IllegalArgumentException("Error finding content package with id: " + packageId);
        }

        String siteId = contentPackage.getContext();
        boolean allowedInSite = siteService.isCurrentUserMemberOfSite(siteId);

        if (!allowedInSite) {
            throw new SecurityException("Current user not allowed in site: " + siteId);
        }

        return siteId;
    }

}
