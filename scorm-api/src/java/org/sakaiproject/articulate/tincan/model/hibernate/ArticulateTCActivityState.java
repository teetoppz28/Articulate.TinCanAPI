package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;

public class ArticulateTCActivityState implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String activityId;
    private String agent;
    private String registration;
    private String stateId;

    public ArticulateTCActivityState() {
    }

    public ArticulateTCActivityState(long id, String activityId, String agent, String registration, String stateId) {
        this.id = id;
        this.activityId = activityId;
        this.agent = agent;
        this.registration = registration;
        this.stateId = stateId;
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
