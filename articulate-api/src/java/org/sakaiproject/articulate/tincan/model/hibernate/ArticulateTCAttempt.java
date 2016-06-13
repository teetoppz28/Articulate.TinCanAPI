package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long contentPackageId;
    private String courseId;
    private String learnerId;
    private String learnerName;
    private Long attemptNumber;
    private Date beginDate;
    private Date lastModifiedDate;

    public ArticulateTCAttempt() {
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(String learnerId) {
        this.learnerId = learnerId;
    }

    public String getLearnerName() {
        return learnerName;
    }

    public void setLearnerName(String learnerName) {
        this.learnerName = learnerName;
    }

    public Long getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Long attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
