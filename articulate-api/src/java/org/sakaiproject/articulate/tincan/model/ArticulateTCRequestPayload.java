package org.sakaiproject.articulate.tincan.model;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCRequestPayload implements ArticulateTCConstants {

    @Setter @Getter private String activityId;
    @Setter @Getter private String agent;
    @Setter @Getter private String content;
    @Setter @Getter private Long contentPackageId;
    @Setter @Getter private String stateId;
    @Setter @Getter private String siteId;
    @Setter @Getter private String userId;

    public ArticulateTCRequestPayload() {
    }

    public ArticulateTCRequestPayload(String contentStr) {
        this(StringUtils.split(contentStr, "&"));
    }

    public ArticulateTCRequestPayload(String[] parameters) {
        populateFields(parameters);
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
        if (this.contentPackageId == null) {
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
            if (StringUtils.startsWith(s, DataKeys.activityId.toString())) {
                setActivityId(s);
                continue;
            }
            if (StringUtils.startsWith(s, DataKeys.agent.toString())) {
                setAgent(s);
                continue;
            }
            if (StringUtils.startsWith(s, DataKeys.content.toString())) {
                setContent(s);
                continue;
            }
            if (StringUtils.startsWith(s, DataKeys.stateId.toString())) {
                setStateId(s);
                continue;
            }
            if (StringUtils.startsWith(s, DataKeys.packageid.toString())) {
                setContentPackageId(Long.parseLong(getValue(s)));
                continue;
            }
        }
    }

}
