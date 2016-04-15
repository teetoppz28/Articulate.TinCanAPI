package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Map;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

public class ArticulateTCActivityState implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    private long id;
    private String activityId;
    private String agent;
    private String content;
    private String registration;
    private String stateId;

    public ArticulateTCActivityState() {
    }

    public ArticulateTCActivityState(long id, String activityId, String agent, String content, String registration, String stateId) {
        this.id = id; // TODO make the ID "userId::siteId::activityId::attemptNumber"
        this.activityId = activityId;
        this.agent = agent;
        this.content = content;
        this.registration = registration;
        this.stateId = stateId;
    }

    public ArticulateTCActivityState(Map<String, String> data) {
        this.activityId = data.get(STATE_DATA_KEY_ACTIVITY_ID);
        this.agent = data.get(STATE_DATA_KEY_AGENT);
        this.content = data.get(STATE_DATA_KEY_CONTENT);
        this.stateId = data.get(STATE_DATA_KEY_STATE_ID);
        this.registration = data.get(STATE_DATA_KEY_SITE_ID);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

}
