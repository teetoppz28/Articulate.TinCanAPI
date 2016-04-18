package org.sakaiproject.articulate.tincan.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCLaunchService;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

public class ArticulateTCLaunchServiceImpl implements ArticulateTCLaunchService, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCLaunchServiceImpl.class);

    @Setter
    private DeveloperHelperService developerHelperService;
    @Setter
    private UserDirectoryService userDirectoryService;

    public void init() {
        /*developerHelperService = (DeveloperHelperService) ComponentManager.get("org.sakaiproject.entitybroker.DeveloperHelperService");
        userDirectoryService = (UserDirectoryService) ComponentManager.get("org.sakaiproject.user.api.UserDirectoryService");*/
    }

    @Override
    public String calculateLaunchParams(String packageId) {
        String userId = calculateCurrentUserId();
        String actor = calculateActor();
        String siteId = calculateCurrentSite();
        String endPoint = calculateEndPoint();

        StringBuilder sb = new StringBuilder("?");
        sb.append(STATE_DATA_KEY_ENDPOINT + "=" + endPoint);
        sb.append("&auth=");
        sb.append("&" + STATE_DATA_KEY_ACTOR + "=" + actor);
        sb.append("&" + STATE_DATA_KEY_SITE_ID + "=" + siteId);
        sb.append("&" + STATE_DATA_KEY_USER_ID + "=" + userId);
        sb.append("&" + STATE_DATA_KEY_PACKAGE_ID + "=" + packageId);

        return sb.toString();
    }

    @Override
    public String calculateActor() {
        try {
            User user = userDirectoryService.getCurrentUser();
            String first = user.getFirstName();
            String last = user.getLastName();
            String email = user.getEmail();
            String actor = "{\"name\":\"" + first + " " + last + "\",\"mbox\":\"mailto:" + email + "\"}";

            return URLEncoder.encode(actor, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error URL encoding actor.", e);
        }

        return "";
    }

    @Override
    public String calculateCurrentSite() {
        String siteId = developerHelperService.getCurrentLocationId();

        try {
            return URLEncoder.encode(siteId, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error URL encoding site ID.", e);
        }

        return "";
    }

    @Override
    public String calculateCurrentUserId() {
        String userId = developerHelperService.getCurrentUserId();

        try {
            return URLEncoder.encode(userId, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error URL encoding user ID.", e);
        }

        return "";
    }

    @Override
    public String calculateEndPoint() {
        String endpoint = EntityView.DIRECT_PREFIX + EntityView.SEPARATOR + REST_PREFIX + EntityView.SEPARATOR + PATH_ACTION + EntityView.SEPARATOR;

        try {
            return URLEncoder.encode(endpoint, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error URL encoding endpoint URL.", e);
        }

        return "";
    }

}
