package org.sakaiproject.scorm.model.tincanapi;

import lombok.Getter;
import lombok.Setter;

public class TinCanAPIManifest {

    @Getter @Setter
    private String activityId;
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String siteId;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String url;

    public boolean isValid() {
        return true;
    }

}
