package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCActivityState implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    @Setter @Getter private String content;
    @Setter @Getter private Long id;
    @Setter @Getter private Long attemptId;
    @Setter @Getter private String stateId;
    @Setter @Getter private Date modified;

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

    public boolean isResume() {
        return StringUtils.equalsIgnoreCase(this.stateId, DataKeys.resume.toString());
    }

    public boolean isComplete() {
        return StringUtils.equalsIgnoreCase(this.stateId, DataKeys.complete.toString());
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
