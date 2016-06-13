package org.sakaiproject.articulate.tincan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCEntityProviderServiceUtils implements ArticulateTCConstants {

    private static final Logger log = LoggerFactory.getLogger(ArticulateTCEntityProviderServiceUtils.class);

    /**
     * Decodes the URL-encoded string
     * 
     * @param str the string to decode
     * @return
     */
    public static String decodeString(String str) {
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
    public static String getRequestPayload(HttpServletRequest request) {
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
    public static String getContentDataFromPayload(String str) {
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
    public static ArticulateTCRequestPayload getPayloadObject(HttpServletRequest request) {
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
    public static ArticulateTCRequestPayload getPayloadObject(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Payload string cannot be blank");
        }

        String decodedPayload = decodeString(str);

        ArticulateTCRequestPayload articulateTCRequestPayload = new ArticulateTCRequestPayload(decodedPayload);
        // get the user ID from the current session
        articulateTCRequestPayload.setUserId(populateCurrentUser());
        // get the site ID from the content package data
        articulateTCRequestPayload.setSiteId(populateCurrentSite(articulateTCRequestPayload.getContentPackageId()));

        return articulateTCRequestPayload;
    }

    /**
     * Gets the user ID from the user associated with the current session
     * 
     * @return
     */
    private static String populateCurrentUser() {
        DeveloperHelperService developerHelperService = (DeveloperHelperService) ComponentManager.get(DeveloperHelperService.class);
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
     * @param contentPackageId
     * @return
     */
    private static String populateCurrentSite(Long contentPackageId) {
        ArticulateTCContentPackageDao articulateTCContentPackageDao = (ArticulateTCContentPackageDao) ComponentManager.get(ArticulateTCContentPackageDao.class);
        ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.load(contentPackageId);

        if (articulateTCContentPackage == null) {
            throw new IllegalArgumentException("Error finding content package with id: " + contentPackageId);
        }

        String siteId = articulateTCContentPackage.getContext();
        boolean allowedInSite = ArticulateTCSecurityUtils.currentUserAllowedAccessToSite(siteId);

        if (!allowedInSite) {
            throw new SecurityException("Current user not allowed in site: " + siteId);
        }

        return siteId;
    }

}
