package org.sakaiproject.articulate.tincan.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.scorm.model.api.ContentPackage;

public class ArticulateTCContentPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private ContentPackage contentPackage;

    public ArticulateTCContentPackage() {
        contentPackage = new ContentPackage();
    }

    public ArticulateTCContentPackage(ArticulateTCMeta articulateTCMeta) {
        this();
        if (articulateTCMeta != null) {
            contentPackage.setContext(articulateTCMeta.getCourseId());
            contentPackage.setTitle(articulateTCMeta.getTitle());
            contentPackage.setResourceId(articulateTCMeta.getId());
            contentPackage.setDeleted(false);
            contentPackage.setReleaseOn(new Date());
            contentPackage.setCreatedOn(new Date());
            contentPackage.setCreatedBy(articulateTCMeta.getCreatedBy());
            contentPackage.setModifiedOn(new Date());
            contentPackage.setModifiedBy(articulateTCMeta.getCreatedBy());
            contentPackage.setTinCanAPI(true);
        }
    }

    public ArticulateTCContentPackage(ArticulateTCMeta articulateTCMeta, String launchUrl) {
        this(articulateTCMeta);
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
        return "ArticulateTCContentPackage :: " +
                    "context: " + contentPackage.getContext() +
                    ", title: " + contentPackage.getTitle() +
                    ", resourceId: " + contentPackage.getResourceId() +
                    ", launchUrl: " + contentPackage.getUrl();
    }

}
