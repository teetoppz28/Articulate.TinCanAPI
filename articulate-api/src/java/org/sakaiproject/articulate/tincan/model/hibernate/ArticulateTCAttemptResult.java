package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCAttemptResult implements ArticulateTCConstants, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long attemptId;
    private Long attemptNumber;
    private Double scaledScore;
    private Date dateCompleted;
    private Date modified;
    private ArticulateTCAttempt articulateTCAttempt;

    public ArticulateTCAttemptResult() {
        this.scaledScore = CONFIGURATION_DEFAULT_SCALED_SCORE;
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

    public Long getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Long attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Double getScaledScore() {
        return scaledScore;
    }

    public void setScaledScore(Double scaledScore) {
        this.scaledScore = scaledScore;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public ArticulateTCAttempt getArticulateTCAttempt() {
        return articulateTCAttempt;
    }

    public void setArticulateTCAttempt(ArticulateTCAttempt articulateTCAttempt) {
        this.articulateTCAttempt = articulateTCAttempt;
    }

}
