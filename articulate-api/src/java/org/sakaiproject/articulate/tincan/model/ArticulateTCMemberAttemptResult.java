package org.sakaiproject.articulate.tincan.model;

import java.io.Serializable;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCMemberAttemptResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String eid;
    private String firstName;
    private String lastName;
    private String fullName;
    private String attemptNumber;
    private String gradebookPointsPossible;
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

    public String getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(String attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getGradebookScore() {
        return gradebookScore;
    }

    public void setGradebookScore(String gradebookScore) {
        this.gradebookScore = gradebookScore;
    }

    public String getGradebookPointsPossible() {
        return gradebookPointsPossible;
    }

    public void setGradebookPointsPossible(String gradebookPointsPossible) {
        this.gradebookPointsPossible = gradebookPointsPossible;
    }

    public String getGradebookDisplay() {
        return (gradebookScore == null ? "-" : gradebookScore) + " / " + gradebookPointsPossible;
    }

}
