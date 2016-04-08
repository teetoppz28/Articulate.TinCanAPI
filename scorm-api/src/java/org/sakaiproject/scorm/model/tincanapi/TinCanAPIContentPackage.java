package org.sakaiproject.scorm.model.tincanapi;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.scorm.model.api.ContentPackage;

public class TinCanAPIContentPackage {

    private static final long serialVersionUID = 1L;

    private ContentPackage contentPackage;

    public TinCanAPIContentPackage() {
        contentPackage = new ContentPackage();
    }

    public TinCanAPIContentPackage(TinCanAPIMeta tinCanAPIMeta) {
        this();
        contentPackage.setContext(tinCanAPIMeta.getCourseId());
        contentPackage.setTitle(tinCanAPIMeta.getTitle());
        contentPackage.setResourceId(tinCanAPIMeta.getId());
        contentPackage.setDeleted(false);
    }

    public TinCanAPIContentPackage(TinCanAPIMeta tinCanAPIMeta, String launchUrl) {
        this(tinCanAPIMeta);
        contentPackage.setUrl(launchUrl);
    }

    public ContentPackage getContentPackage() {
        return contentPackage;
    }

    public boolean isValid() {
        if (this.contentPackage == null) {
            return false;
        }
        if (StringUtils.isBlank(contentPackage.getContext())) {
            return false;
        }
        if (StringUtils.isBlank(contentPackage.getTitle())) {
            return false;
        }
        if (StringUtils.isBlank(contentPackage.getResourceId())) {
            return false;
        }
        if (StringUtils.isBlank(contentPackage.getUrl())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "TinCanAPIManifest :: " +
                    "context: " + contentPackage.getContext() +
                    ", title: " + contentPackage.getTitle() +
                    ", resourceId: " + contentPackage.getResourceId() +
                    ", launchUrl: " + contentPackage.getUrl();
    }

}
