package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Map;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

public class ArticulateTCActivityState implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    /**
     * ID Format: userId::siteId::packageId
     */
    private String id;
    private String activityId;
    private String agent;
    private String content;
    private String registration;
    private String stateId;

    public ArticulateTCActivityState() {
    }

    public ArticulateTCActivityState(String id, String activityId, String agent, String content, String registration, String stateId) {
        this.id = id;
        this.activityId = activityId;
        this.agent = agent;
        this.content = content;
        this.registration = registration;
        this.stateId = stateId;
    }

    public ArticulateTCActivityState(Map<String, String> data) {
        this.id = buildId(data.get(STATE_DATA_KEY_ID), data.get(STATE_DATA_KEY_SITE_ID), data.get(STATE_DATA_KEY_PACKAGE_ID));
        this.activityId = data.get(STATE_DATA_KEY_ACTIVITY_ID);
        this.agent = data.get(STATE_DATA_KEY_AGENT);
        this.content = data.get(STATE_DATA_KEY_CONTENT);
        this.stateId = data.get(STATE_DATA_KEY_STATE_ID);
        this.registration = data.get(STATE_DATA_KEY_SITE_ID);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    /**
     * Builds the ID in the specified format
     * e.g. userID::siteID::packageID
     * 
     * @param userId
     * @param siteId
     * @param packageId
     * @return
     */
    public String buildId(String userId, String siteId, String packageId) {
        return userId + STATE_DATA_ID_SEPARATOR + siteId + STATE_DATA_ID_SEPARATOR + packageId;
    }

}
