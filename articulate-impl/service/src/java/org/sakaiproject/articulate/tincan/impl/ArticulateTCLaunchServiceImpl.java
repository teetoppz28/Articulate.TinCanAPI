package org.sakaiproject.articulate.tincan.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import lombok.Setter;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCLaunchServiceImpl implements ArticulateTCLaunchService, ArticulateTCConstants {

    private final Logger log = LoggerFactory.getLogger(ArticulateTCLaunchServiceImpl.class);

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

        StringBuilder sb = new StringBuilder("?")
            .append(STATE_DATA_KEY_ENDPOINT + "=" + endPoint)
            .append("&").append(STATE_DATA_KEY_AUTH).append("=")
            .append("&").append(STATE_DATA_KEY_ACTOR).append("=").append(actor)
            .append("&").append(STATE_DATA_KEY_PACKAGE_ID).append("=").append(packageId);

        return sb.toString();
    }

    @Override
    public String calculateActor() {
        try {
            User user = userDirectoryService.getCurrentUser();
            String first = user.getFirstName();
            String last = user.getLastName();
            String email = user.getEmail();
            StringBuilder actor = new StringBuilder("{\"name\":\"")
                .append(first)
                .append(" ")
                .append(last)
                .append("\",\"mbox\":\"mailto:")
                .append(email)
                .append("\"}");

            return URLEncoder.encode(actor.toString(), DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Error URL encoding actor.", e);
        }

        return "";
    }

    @Override
    public String calculateEndPoint() {
        StringBuilder endpoint = new StringBuilder(EntityView.DIRECT_PREFIX)
            .append(EntityView.SEPARATOR)
            .append(REST_PREFIX)
            .append(EntityView.SEPARATOR);

        try {
            return URLEncoder.encode(endpoint.toString(), DEFAULT_ENCODING);
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
        ArticulateTCAttempt latestAttempt = articulateTCAttemptDao.lookupNewest(contentPackageId, userId);

        ArticulateTCAttempt newAttempt = new ArticulateTCAttempt();
        newAttempt.setContentPackageId(contentPackageId);
        newAttempt.setCourseId(siteId);
        newAttempt.setLearnerId(userId);
        newAttempt.setBeginDate(new Date());
        newAttempt.setAttemptNumber(latestAttempt == null ? 1 : latestAttempt.getAttemptNumber() + 1);

        articulateTCAttemptDao.save(newAttempt);

        addAttemptResult(newAttempt);

        StringBuilder eventRef = new StringBuilder("articulate/tc/site/")
            .append(articulateTCContentPackage.getContext())
            .append("/user/")
            .append(userId)
            .append("/packageId/")
            .append(articulateTCContentPackage.getContentPackageId());
        eventTrackingService.post(eventTrackingService.newEvent(SAKAI_EVENT_LAUNCH, eventRef.toString() , true));
    }

    @Override
    public void addAttemptResult(ArticulateTCAttempt articulateTCAttempt) {
        if (articulateTCAttempt == null) {
            log.error("Error: the attempt is null.");
            return;
        }

        ArticulateTCAttemptResult articulateTCAttemptResult = new ArticulateTCAttemptResult();
        articulateTCAttemptResult.setAttemptId(articulateTCAttempt.getId());
        articulateTCAttemptResult.setAttemptNumber(articulateTCAttempt.getAttemptNumber());

        articulateTCAttemptResultDao.save(articulateTCAttemptResult);
    }

}
