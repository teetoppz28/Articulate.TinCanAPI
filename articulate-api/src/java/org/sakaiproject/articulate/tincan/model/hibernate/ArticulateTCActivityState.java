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
    private Long contentPackageId;
    private String registration;
    private String stateId;
    private String userId;
    private boolean deleted;
    private Date modified;
    private ArticulateTCContentPackage articulateTCContentPackage;

    public ArticulateTCActivityState() {
        this.deleted = CONFIGURATION_DEFAULT_ACTIVITY_STATE_DELETED;
    }

    public ArticulateTCActivityState(ArticulateTCRequestPayload articulateTCRequestPayload) {
        this.content = articulateTCRequestPayload.getContent();
        this.contentPackageId = articulateTCRequestPayload.getContentPackageId();
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

    public Long getContentPackageId() {
        return contentPackageId;
    }

    public void setContentPackageId(Long contentPackageId) {
        this.contentPackageId = contentPackageId;
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

    public ArticulateTCContentPackage getArticulateTCContentPackage() {
        return articulateTCContentPackage;
    }

    public void setArticulateTCContentPackage(ArticulateTCContentPackage articulateTCContentPackage) {
        this.articulateTCContentPackage = articulateTCContentPackage;
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
