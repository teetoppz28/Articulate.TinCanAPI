package org.sakaiproject.scorm.model.tincanapi;

import org.apache.commons.lang.StringUtils;

public class TinCanAPIMeta {

    private String courseId;
    private String id;
    private String title;

    public final static String ATTR_COURSEID = "courseid";
    public final static String ATTR_ID = "id";
    public final static String ATTR_TITLE = "title";
    
    public TinCanAPIMeta() {
    }

    public TinCanAPIMeta(String courseId, String id, String title) {
        this.courseId = courseId;
        this.id = id;
        this.title = title;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
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
        if (StringUtils.isBlank(this.id)) {
            return false;
        }
        if (StringUtils.isBlank(this.title)) {
            return false;
        }

        return true;
    }
}
