package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMeta;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCContentPackage implements ArticulateTCConstants, Serializable {

    private static final long serialVersionUID = 1L;

    @Setter @Getter private Long contentPackageId;
    @Setter @Getter private String title;
    @Setter @Getter private String resourceId;
    @Setter @Getter private String context;
    @Setter @Getter private String url;
    @Setter @Getter private String recordType;
    @Setter @Getter private Date releaseOn;
    @Setter @Getter private Date dueOn;
    @Setter @Getter private Date acceptUntil;
    @Setter @Getter private Date createdOn;
    @Setter @Getter private String createdBy;
    @Setter @Getter private Date modifiedOn;
    @Setter @Getter private String modifiedBy;
    @Setter @Getter private int numberOfTries;
    @Setter @Getter private boolean isDeleted;
    @Setter @Getter private Long assignmentId;
    @Setter @Getter private String gradebookItemTitle;
    @Setter @Getter private boolean graded;
    @Setter @Getter private Double points;

    public ArticulateTCContentPackage() {
        this.numberOfTries = CONFIGURATION_DEFAULT_NUMBER_OF_TRIES_UNLIMITED;
        this.graded = CONFIGURATION_DEFAULT_IS_GRADED;
        this.points = CONFIGURATION_DEFAULT_POINTS;
        this.recordType = CONFIGURATION_RECORD_SCORE_TYPE_LATEST;
    }

    public ArticulateTCContentPackage(ArticulateTCMeta articulateTCMeta) {
        this();

        if (articulateTCMeta != null) {
            this.context = articulateTCMeta.getCourseId();
            this.title = articulateTCMeta.getTitle();
            this.resourceId = articulateTCMeta.getId();
            this.isDeleted = false;
            this.releaseOn = new Date();
            this.createdOn = new Date();
            this.createdBy = articulateTCMeta.getCreatedBy();
            this.modifiedOn = new Date();
            this.modifiedBy = articulateTCMeta.getCreatedBy();
        }
    }

    public ArticulateTCContentPackage(ArticulateTCMeta articulateTCMeta, String launchUrl) {
        this(articulateTCMeta);
        this.url = launchUrl;
    }

    /**
     * Is only the best score recorded?
     * 
     * @return
     */
    public boolean isRecordBest() {
        return StringUtils.equals(recordType, CONFIGURATION_RECORD_SCORE_TYPE_BEST);
    }

    /**
     * Can the user have unlimited attempts?
     * 
     * @return
     */
    public boolean isUnlimitedAttempts() {
        return this.numberOfTries == -1;
    }

    /**
     * Are all required fields non-blank?
     * 
     * @return
     */
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
        return "ArticulateTCContentPackage :: " +
            "id: " + getContentPackageId() +
            ", context: " + getContext() +
            ", title: " + getTitle() +
            ", resourceId: " + getResourceId() +
            ", launchUrl: " + getUrl();
    }

}
