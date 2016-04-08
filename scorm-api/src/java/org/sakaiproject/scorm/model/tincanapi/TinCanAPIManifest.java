package org.sakaiproject.scorm.model.tincanapi;

import org.sakaiproject.scorm.model.api.ContentPackage;


public class TinCanAPIManifest extends ContentPackage {

    private static final long serialVersionUID = 1L;

    public TinCanAPIManifest() {
        super();
    }

    public TinCanAPIManifest (TinCanAPIMeta tinCanAPIMeta) {
        super();
        setContext(tinCanAPIMeta.getCourseId());
        setTitle(tinCanAPIMeta.getTitle());
        setResourceId(tinCanAPIMeta.getId());
        setDeleted(false);
    }

    public boolean isValid() {
        return true;
    }

}
