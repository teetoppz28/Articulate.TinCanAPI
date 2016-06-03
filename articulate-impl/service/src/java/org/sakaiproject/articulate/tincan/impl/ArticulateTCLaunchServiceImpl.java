package org.sakaiproject.articulate.tincan.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCLaunchService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptResultDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCLaunchServiceImpl implements ArticulateTCLaunchService, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCLaunchServiceImpl.class);

    @Setter
    private ArticulateTCAttemptDao articulateTCAttemptDao;

    @Setter
    private ArticulateTCAttemptResultDao articulateTCAttemptResultDao;

    @Setter
    private ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @Setter
    private EventTrackingService eventTrackingService;

    @Setter
    private UserDirectoryService userDirectoryService;

    @Override
    public String calculateLaunchParams(String packageId) {
        String actor = calculateActor();
        String endPoint = calculateEndPoint();

        StringBuilder sb = new StringBuilder("?");
        sb.append(STATE_DATA_KEY_ENDPOINT + "=" + endPoint);
        sb.append("&" + STATE_DATA_KEY_AUTH + "=");
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
        Long contentPackageId = Long.parseLong(packageId);
        ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.load(contentPackageId);

        if (articulateTCContentPackage == null) {
            throw new IllegalArgumentException("Error:: no content package found with ID: " + packageId);
        }

        String siteId = articulateTCContentPackage.getContext();
        User user = userDirectoryService.getCurrentUser();
        String userId = user.getId();
        String displayName = user.getDisplayName();
        ArticulateTCAttempt latestAttempt = articulateTCAttemptDao.lookupNewest(contentPackageId, userId);

        ArticulateTCAttempt newAttempt = new ArticulateTCAttempt();
        newAttempt.setContentPackageId(contentPackageId);
        newAttempt.setCourseId(siteId);
        newAttempt.setLearnerId(userId);
        newAttempt.setLearnerName(displayName);
        newAttempt.setBeginDate(new Date());
        newAttempt.setAttemptNumber(latestAttempt == null ? 1 : latestAttempt.getAttemptNumber() + 1);

        articulateTCAttemptDao.save(newAttempt);

        addAttemptResult(newAttempt.getId(), newAttempt.getAttemptNumber());

        eventTrackingService.post(eventTrackingService.newEvent(SAKAI_EVENT_LAUNCH, "articulate/tc/site/" + articulateTCContentPackage.getContext() + "/user/" + userId + "/packageId/" + articulateTCContentPackage.getContentPackageId(), true));
    }

    @Override
    public void addAttemptResult(Long attemptId, Long attemptNumber) {
        if (attemptId == null) {
            /*
             * attempt object ID is null (probably not persisted to the db yet),
             * we'll create this result object later
             */
            log.error("Error: the attempt ID is null.");
            return;
        }

        ArticulateTCAttemptResult articulateTCAttemptResult = new ArticulateTCAttemptResult();
        articulateTCAttemptResult.setAttemptId(attemptId);
        articulateTCAttemptResult.setAttemptNumber(attemptNumber);

        articulateTCAttemptResultDao.save(articulateTCAttemptResult);
    }

}
