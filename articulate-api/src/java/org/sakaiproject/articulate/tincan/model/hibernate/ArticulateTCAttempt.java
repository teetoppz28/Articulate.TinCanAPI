/*
 * #%L
 * SCORM API
 * %%
 * Copyright (C) 2007 - 2016 Sakai Project
 * %%
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *             http://opensource.org/licenses/ecl2
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
    private ArticulateTCContentPackage articulateTCContentPackage;
    private Set<ArticulateTCAttemptResult> articulateTCAttemptResults;

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

    public ArticulateTCContentPackage getArticulateTCContentPackage() {
        return articulateTCContentPackage;
    }

    public void setArticulateTCContentPackage(ArticulateTCContentPackage articulateTCContentPackage) {
        this.articulateTCContentPackage = articulateTCContentPackage;
    }

    public Set<ArticulateTCAttemptResult> getArticulateTCAttemptResults() {
        return articulateTCAttemptResults;
    }

    public void setArticulateTCAttemptResults(Set<ArticulateTCAttemptResult> articulateTCAttemptResults) {
        this.articulateTCAttemptResults = articulateTCAttemptResults;
    }

}
