package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

public class ArticulateTCAttemptResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long attemptId;
    private int attemptNumber;
    private Double scaledScore;
    private Date dateCompleted;
    private Date modified;

    public ArticulateTCAttemptResult() {
        this.scaledScore = 0.0;
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

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
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

}
