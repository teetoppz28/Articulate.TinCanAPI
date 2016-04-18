package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;

public class ArticulateTCActivityState implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    private String activityId;
    private String agent;
    private String content;
    private long id;
    private String packageId;
    private String registration;
    private String stateId;
    private String userId;
    private boolean deleted = false;
    private Date created;
    private Date modified;

    public ArticulateTCActivityState() {
    }

    public ArticulateTCActivityState(ArticulateTCRequestPayload articulateTCRequestPayload) {
        this.activityId = articulateTCRequestPayload.getActivityId();
        this.agent = articulateTCRequestPayload.getAgent();
        this.content = articulateTCRequestPayload.getContent();
        this.packageId = articulateTCRequestPayload.getPackageId();
        this.registration = articulateTCRequestPayload.getSiteId();
        this.stateId = articulateTCRequestPayload.getStateId();
        this.userId = articulateTCRequestPayload.getUserId();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * Updates only the allowed updatable db fields
     * 
     * @param articulateTCRequestPayload the {@link ArticulateTCRequestPayload} object
     */
    public void updateMutableFields(ArticulateTCRequestPayload articulateTCRequestPayload) {
        this.content = articulateTCRequestPayload.getContent();
        this.stateId = articulateTCRequestPayload.getStateId();
    }

}
