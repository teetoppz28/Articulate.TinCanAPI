package org.sakaiproject.scorm.model.tincanapi;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.scorm.model.api.ContentPackage;

public class TinCanAPIContentPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private ContentPackage contentPackage;

    public TinCanAPIContentPackage() {
        contentPackage = new ContentPackage();
    }

    public TinCanAPIContentPackage(TinCanAPIMeta tinCanAPIMeta) {
        this();
        if (tinCanAPIMeta != null) {
            contentPackage.setContext(tinCanAPIMeta.getCourseId());
            contentPackage.setTitle(tinCanAPIMeta.getTitle());
            contentPackage.setResourceId(tinCanAPIMeta.getId());
            contentPackage.setDeleted(false);
            contentPackage.setReleaseOn(new Date());
            contentPackage.setCreatedOn(new Date());
            contentPackage.setCreatedBy(tinCanAPIMeta.getCreatedBy());
            contentPackage.setModifiedOn(new Date());
            contentPackage.setModifiedBy(tinCanAPIMeta.getCreatedBy());
        }
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
