package org.sakaiproject.articulate.tincan.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCLaunchService;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.scorm.dao.api.AttemptDao;
import org.sakaiproject.scorm.model.api.Attempt;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

public abstract class ArticulateTCLaunchServiceImpl implements ArticulateTCLaunchService, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCLaunchServiceImpl.class);

    @Setter
    private DeveloperHelperService developerHelperService;
    @Setter
    private UserDirectoryService userDirectoryService;

    protected abstract AttemptDao attemptDao();

    public void init() {
    }

    @Override
    public String calculateLaunchParams(String packageId) {
        String actor = calculateActor();
        String endPoint = calculateEndPoint();

        StringBuilder sb = new StringBuilder("?");
        sb.append(STATE_DATA_KEY_ENDPOINT + "=" + endPoint);
        sb.append("&auth=");
        sb.append("&" + STATE_DATA_KEY_ACTOR + "=" + actor);
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
    public String calculateEndPoint() {
        String endpoint = EntityView.DIRECT_PREFIX + EntityView.SEPARATOR + REST_PREFIX + EntityView.SEPARATOR;

        try {
            return URLEncoder.encode(endpoint, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error URL encoding endpoint URL.", e);
        }

        return "";
    }

    @Override
    public void addAttempt(String packageId) {
        String siteId = developerHelperService.getCurrentLocationId();
        User user = userDirectoryService.getCurrentUser();
        String userId = user.getId();
        String displayName = user.getDisplayName();
        Long contentPackageId = Long.parseLong(packageId);
        Attempt latestAttempt = attemptDao().lookupNewest(contentPackageId, userId);

        Attempt newAttempt = new Attempt();
        newAttempt.setContentPackageId(contentPackageId);
        newAttempt.setCourseId(siteId);
        newAttempt.setLearnerId(userId);
        newAttempt.setLearnerName(displayName);
        newAttempt.setBeginDate(new Date());
        newAttempt.setAttemptNumber(latestAttempt == null ? 1 :latestAttempt.getAttemptNumber() + 1);

        attemptDao().save(newAttempt);
    }

}
