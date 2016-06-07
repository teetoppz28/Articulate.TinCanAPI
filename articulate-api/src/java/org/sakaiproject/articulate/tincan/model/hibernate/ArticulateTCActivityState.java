package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCActivityState implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    private String content;
    private Long id;
    private Long attemptId;
    private String stateId;
    private Date modified;

    public ArticulateTCActivityState() {
    }

    public ArticulateTCActivityState(ArticulateTCRequestPayload articulateTCRequestPayload) {
        this.content = articulateTCRequestPayload.getContent();
        this.stateId = articulateTCRequestPayload.getStateId();
    }

    public ArticulateTCActivityState(ArticulateTCRequestPayload articulateTCRequestPayload, Long attemptId) {
        this(articulateTCRequestPayload);
        this.attemptId = attemptId;
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

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public boolean isResume() {
        return StringUtils.equalsIgnoreCase(this.stateId, STATE_DATA_KEY_STATE_ID_RESUME);
    }

    public boolean isComplete() {
        return StringUtils.equalsIgnoreCase(this.stateId, STATE_DATA_KEY_STATE_ID_COMPLETE);
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
