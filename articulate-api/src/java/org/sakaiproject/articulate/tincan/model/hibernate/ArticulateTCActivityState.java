package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCActivityState implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    private String content;
    private Long id;
    private String packageId;
    private String registration;
    private String stateId;
    private String userId;
    private boolean deleted;
    private Date modified;

    public ArticulateTCActivityState() {
        this.deleted = CONFIGURATION_DEFAULT_ACTIVITY_STATE_DELETED;
    }

    public ArticulateTCActivityState(ArticulateTCRequestPayload articulateTCRequestPayload) {
        this.content = articulateTCRequestPayload.getContent();
        this.packageId = articulateTCRequestPayload.getPackageId();
        this.registration = articulateTCRequestPayload.getSiteId();
        this.stateId = articulateTCRequestPayload.getStateId();
        this.userId = articulateTCRequestPayload.getUserId();
        this.deleted = CONFIGURATION_DEFAULT_ACTIVITY_STATE_DELETED;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * Updates only the allowed modifiable database fields
     * 
     * @param articulateTCRequestPayload the {@link ArticulateTCRequestPayload} object
     */
    public void updateMutableFields(ArticulateTCRequestPayload articulateTCRequestPayload) {
        this.content = articulateTCRequestPayload.getContent();
        this.stateId = articulateTCRequestPayload.getStateId();
    }

}
