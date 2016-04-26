package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

public class ArticulateTCContentPackageSettings implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    private long id;
    private Long gradebookItemId;
    private String gradebookItemTitle;
    private boolean graded;
    private Long packageId;
    private Double points;
    private Date modified;

    public ArticulateTCContentPackageSettings() {
        this.graded = CONFIGURATION_DEFAULT_IS_GRADED;
        this.points = CONFIGURATION_DEFAULT_POINTS;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    public Long getGradebookItemId() {
        return gradebookItemId;
    }

    public void setGradebookItemId(Long gradebookItemId) {
        this.gradebookItemId = gradebookItemId;

        this.graded = gradebookItemId != null;
    }

    public String getGradebookItemTitle() {
        return gradebookItemTitle;
    }

    public void setGradebookItemTitle(String gradebookItemTitle) {
        this.gradebookItemTitle = gradebookItemTitle;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}
