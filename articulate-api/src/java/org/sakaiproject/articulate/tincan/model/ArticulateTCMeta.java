package org.sakaiproject.articulate.tincan.model;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

public class ArticulateTCMeta implements ArticulateTCConstants {

    private String courseId;
    private String createdBy;
    private String id;
    private String title;

    public ArticulateTCMeta() {
    }

    public ArticulateTCMeta(String courseId, String createdBy, String id, String title) {
        this.courseId = courseId;
        this.createdBy = createdBy;
        this.id = id;
        this.title = title;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isValid() {
        if (StringUtils.isBlank(this.courseId)) {
            return false;
        }
        if (StringUtils.isBlank(this.createdBy)) {
            return false;
        }
        if (StringUtils.isBlank(this.id)) {
            return false;
        }
        if (StringUtils.isBlank(this.title)) {
            return false;
        }

        return true;
    }

}
