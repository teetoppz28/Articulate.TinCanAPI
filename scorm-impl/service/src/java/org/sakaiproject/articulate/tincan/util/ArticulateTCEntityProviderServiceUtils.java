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

public abstract class ArticulateTCEntityProviderServiceUtils implements ArticulateTCConstants {

    private static Log log = LogFactory.getLog(ArticulateTCEntityProviderServiceUtils.class);

    @Setter
    private DeveloperHelperService developerHelperService;

    protected abstract ContentPackageDao contentPackageDao();

    /**
     * Decodes the URL-encoded string
     * 
     * @param str the string to decode
     * @return
     */
    public String decodeString(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        try {
            return URLDecoder.decode(str, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error unencoding the string", e);
        }

        return str;
    }

    /**
     * Gets the payload from the {@link HttpServletRequest} object
     * 
     * @param request
     * @return the payload string
     */
    public String getRequestPayload(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest cannot be null");
        }

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
            log.error("Error retrieving the request payload", e);
        }

        return content;
    }

    /**
     * Gets the JSON string from the content variable in the payload
     * 
     * @param str the payload string from the request
     * @return the content data
     */
    public String getContentDataFromPayload(String str) {
        ArticulateTCRequestPayload articulateTCRequestPayload = getPayloadObject(str);

        if (StringUtils.isBlank(articulateTCRequestPayload.getContent())) {
            // should not get here... there must not be a "content" portion in the payload
            throw new IllegalArgumentException("Request payload does not contain a valid content statement string");
        }

        return articulateTCRequestPayload.getContent();
    }

    /**
     * Retrieves the payload as an object from the request
     * 
     * @param request
     * @return
     */
    public ArticulateTCRequestPayload getPayloadObject(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request object cannot be null");
        }

        return getPayloadObject(getRequestPayload(request));
    }

    /**
     * Retrieves the payload as an object from the payload string
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
        // get the user ID from the current session
        articulateTCRequestPayload.setUserId(populateCurrentUser());
        // get the site ID from the content package data
        articulateTCRequestPayload.setSiteId(populateCurrentSite(articulateTCRequestPayload.getPackageId()));

        return articulateTCRequestPayload;
    }

    /**
     * Gets the user ID from the user associated with the current session
     * 
     * @return
     */
    private String populateCurrentUser() {
        String userId = developerHelperService.getCurrentUserId();

        if (StringUtils.isBlank(userId)) {
            throw new SecurityException("Error: no current user is defined");
        }

        return userId;
    }

    /**
     * Gets the site ID associated with the given package ID
     * Ensures the current user is allowed to access the site
     * 
     * @param packageId
     * @return
     */
    private String populateCurrentSite(String packageId) {
        ContentPackage contentPackage = contentPackageDao().load(Long.parseLong(packageId));

        if (contentPackage == null) {
            throw new IllegalArgumentException("Error finding content package with id: " + packageId);
        }

        String siteId = contentPackage.getContext();
        boolean allowedInSite = ArticulateTCSecurityUtils.currentUserAllowedAccessToSite(siteId);

        if (!allowedInSite) {
            throw new SecurityException("Current user not allowed in site: " + siteId);
        }

        return siteId;
    }

}
