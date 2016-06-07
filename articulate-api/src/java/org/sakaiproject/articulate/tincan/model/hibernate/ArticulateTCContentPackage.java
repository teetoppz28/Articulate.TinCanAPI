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

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMeta;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCContentPackage implements ArticulateTCConstants, Serializable {

    private static final long serialVersionUID = 1L;

    private Long contentPackageId;
    private String title;
    private String resourceId;
    private String context;
    private String url;
    private String recordType;
    private Date releaseOn;
    private Date dueOn;
    private Date acceptUntil;
    private Date createdOn;
    private String createdBy;
    private Date modifiedOn;
    private String modifiedBy;
    private int numberOfTries;
    private boolean isDeleted;
    private Long assignmentId;
    private String gradebookItemTitle;
    private boolean graded;
    private Double points;

    public ArticulateTCContentPackage() {
        this.numberOfTries = CONFIGURATION_DEFAULT_NUMBER_OF_TRIES_UNLIMITED;
        this.graded = CONFIGURATION_DEFAULT_IS_GRADED;
        this.points = CONFIGURATION_DEFAULT_POINTS;
        this.recordType = CONFIGURATION_RECORD_SCORE_TYPE_LATEST;
    }

    public ArticulateTCContentPackage(ArticulateTCMeta articulateTCMeta) {
        this();

        if (articulateTCMeta != null) {
            setContext(articulateTCMeta.getCourseId());
            setTitle(articulateTCMeta.getTitle());
            setResourceId(articulateTCMeta.getId());
            setDeleted(false);
            setReleaseOn(new Date());
            setCreatedOn(new Date());
            setCreatedBy(articulateTCMeta.getCreatedBy());
            setModifiedOn(new Date());
            setModifiedBy(articulateTCMeta.getCreatedBy());
        }
    }

    public ArticulateTCContentPackage(ArticulateTCMeta articulateTCMeta, String launchUrl) {
        this(articulateTCMeta);
        setUrl(launchUrl);
    }

    public Long getContentPackageId() {
        return contentPackageId;
    }

    public void setContentPackageId(Long contentPackageId) {
        this.contentPackageId = contentPackageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Date getReleaseOn() {
        return releaseOn;
    }

    public void setReleaseOn(Date releaseOn) {
        this.releaseOn = releaseOn;
    }

    public Date getDueOn() {
        return dueOn;
    }

    public void setDueOn(Date dueOn) {
        this.dueOn = dueOn;
    }

    public Date getAcceptUntil() {
        return acceptUntil;
    }

    public void setAcceptUntil(Date acceptUntil) {
        this.acceptUntil = acceptUntil;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;

        this.graded = assignmentId != null;
    }

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    public String getGradebookItemTitle() {
        return gradebookItemTitle;
    }

    public void setGradebookItemTitle(String gradebookItemTitle) {
        this.gradebookItemTitle = gradebookItemTitle;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public boolean isRecordBest() {
        return StringUtils.equals(recordType, CONFIGURATION_RECORD_SCORE_TYPE_BEST);
    }

    public boolean isValid() {
        if (StringUtils.isBlank(getContext())) {
            return false;
        }
        if (StringUtils.isBlank(getTitle())) {
            return false;
        }
        if (StringUtils.isBlank(getResourceId())) {
            return false;
        }
        if (StringUtils.isBlank(getUrl())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ArticulateTCContentPackage :: " +
                    "id: " + getContentPackageId() +
                    ", context: " + getContext() +
                    ", title: " + getTitle() +
                    ", resourceId: " + getResourceId() +
                    ", launchUrl: " + getUrl();
    }

}
