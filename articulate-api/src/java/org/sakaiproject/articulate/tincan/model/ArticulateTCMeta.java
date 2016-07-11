package org.sakaiproject.articulate.tincan.model;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCMeta implements ArticulateTCConstants {

    @Setter @Getter private String courseId;
    @Setter @Getter private String createdBy;
    @Setter @Getter private String id;
    @Setter @Getter private String title;

    public ArticulateTCMeta() {
    }

    public ArticulateTCMeta(String courseId, String createdBy, String id, String title) {
        this.courseId = courseId;
        this.createdBy = createdBy;
        this.id = id;
        this.title = title;
    }

    /**
     * Are all required fields non-blank?
     * 
     * @return
     */
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
