package org.sakaiproject.scorm.model.api;

import org.apache.commons.lang.StringUtils;

public class TinCanAPIManifest {

    private String activityId;
    private Long id;
    private String siteId;
    private String title;
    private String url;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValid() {
        if (id == null) {
            //return false;
        }

        if (StringUtils.isBlank(this.title)) {
            return false;
        }

        return true;
    }
}
