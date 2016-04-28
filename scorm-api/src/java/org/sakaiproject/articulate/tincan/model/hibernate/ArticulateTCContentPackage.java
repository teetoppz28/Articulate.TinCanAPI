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
import org.sakaiproject.scorm.model.api.ContentPackage;

public class ArticulateTCContentPackage extends ContentPackage implements ArticulateTCConstants, Serializable {

    private static final long serialVersionUID = 1L;

    private Long assignmentId;
    private String gradebookItemTitle;
    private boolean graded;
    private Double points;

    public ArticulateTCContentPackage() {
        super();
        this.graded = CONFIGURATION_DEFAULT_IS_GRADED;
        this.points = CONFIGURATION_DEFAULT_POINTS;
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
                    "context: " + getContext() +
                    ", title: " + getTitle() +
                    ", resourceId: " + getResourceId() +
                    ", launchUrl: " + getUrl();
    }

}
