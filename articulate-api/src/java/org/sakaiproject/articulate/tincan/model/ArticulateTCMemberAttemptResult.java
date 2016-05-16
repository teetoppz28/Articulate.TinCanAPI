package org.sakaiproject.articulate.tincan.model;

import java.io.Serializable;
import java.util.List;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;

public class ArticulateTCMemberAttemptResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String eid;
    private String firstName;
    private String lastName;
    private String fullName;
    private ArticulateTCAttempt articulateTCAttempt;
    private List<ArticulateTCAttemptResult> articulateTCAttemptResults;
    private String gradebookScore;

    public ArticulateTCMemberAttemptResult() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArticulateTCAttempt getArticulateTCAttempt() {
        return articulateTCAttempt;
    }

    public void setArticulateTCAttempt(ArticulateTCAttempt articulateTCAttempt) {
        this.articulateTCAttempt = articulateTCAttempt;
    }

    public List<ArticulateTCAttemptResult> getArticulateTCAttemptResults() {
        return articulateTCAttemptResults;
    }

    public void setArticulateTCAttemptResults(
            List<ArticulateTCAttemptResult> articulateTCAttemptResults) {
        this.articulateTCAttemptResults = articulateTCAttemptResults;
    }

    public String getGradebookScore() {
        return gradebookScore;
    }

    public void setGradebookScore(String gradebookScore) {
        this.gradebookScore = gradebookScore;
    }

}
