package org.sakaiproject.scorm.model.tincanapi;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.scorm.model.api.ContentPackage;

public class TinCanAPIContentPackage extends ContentPackage {

    private static final long serialVersionUID = 1L;

    public TinCanAPIContentPackage() {
        super();
    }

    public TinCanAPIContentPackage(TinCanAPIMeta tinCanAPIMeta) {
        super();
        setContext(tinCanAPIMeta.getCourseId());
        setTitle(tinCanAPIMeta.getTitle());
        setResourceId(tinCanAPIMeta.getId());
        setDeleted(false);
    }

    public TinCanAPIContentPackage(TinCanAPIMeta tinCanAPIMeta, String launchUrl) {
        this(tinCanAPIMeta);
        setUrl(launchUrl);
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
        return "TinCanAPIManifest :: context: " + getContext() + ", title: " + getTitle() + ", resourceId: " + getResourceId() + ", launchUrl: " + getUrl();
    }

}
