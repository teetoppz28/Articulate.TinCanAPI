package org.sakaiproject.articulate.tincan.model;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

public class ArticulateTCRequestPayload implements ArticulateTCConstants {

    private String activityId;
    private String agent;
    private String content;
    private String packageId;
    private String stateId;
    private String siteId;
    private String userId;

    public ArticulateTCRequestPayload() {
    }

    public ArticulateTCRequestPayload(String contentStr) {
        this(StringUtils.split(contentStr, "&"));
    }

    public ArticulateTCRequestPayload(String[] parameters) {
        populateFields(parameters);
    }

    public ArticulateTCRequestPayload(String activityId, String agent, String content, String packageId, String stateId, String siteId, String userId) {
        this.activityId = activityId;
        this.agent = agent;
        this.content = content;
        this.packageId = packageId;
        this.stateId = stateId;
        this.siteId = siteId;
        this.userId = userId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = getValue(activityId);
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = getValue(agent);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = getValue(content);
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = getValue(packageId);
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = getValue(stateId);
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = getValue(siteId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = getValue(userId);
    }

    /**
     * Removes the parameter key and "=" sign from the string
     * e.g. "siteid=site1" => "site1"
     * 
     * @param parameter
     * @return
     */
    public String getValue(String parameter) {
        return StringUtils.substringAfter(parameter, "=");
    }

    /**
     * Ensure all required fields are valid for this object
     * 
     * @return true, if all required fields have a value (which they must to save to the db)
     */
    public boolean isValid() {
        if (StringUtils.isBlank(this.userId)) {
            return false;
        }
        if (StringUtils.isBlank(this.siteId)) {
            return false;
        }
        if (StringUtils.isBlank(this.packageId)) {
            return false;
        }

        return true;
    }

    /**
     * Populates the fields from the string array of parameters
     * 
     * @param parameters
     */
    public void populateFields(String[] parameters) {
        for (String s : parameters) {
            if (StringUtils.startsWith(s, STATE_DATA_KEY_ACTIVITY_ID)) {
                setActivityId(s);
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_AGENT)) {
                setAgent(s);
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_CONTENT)) {
                setContent(s);
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_STATE_ID)) {
                setStateId(s);
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_SITE_ID)) {
                setSiteId(s);
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_USER_ID)) {
                setUserId(s);
                continue;
            }
            if (StringUtils.startsWith(s, STATE_DATA_KEY_PACKAGE_ID)) {
                setPackageId(s);
                continue;
            }
        }
    }

}
