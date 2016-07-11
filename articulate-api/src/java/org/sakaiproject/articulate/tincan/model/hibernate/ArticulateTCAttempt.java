package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter @Getter private Long id;
    @Setter @Getter private Long contentPackageId;
    @Setter @Getter private String courseId;
    @Setter @Getter private String learnerId;
    @Setter @Getter private Long attemptNumber;
    @Setter @Getter private Date beginDate;
    @Setter @Getter private Date lastModifiedDate;

    public ArticulateTCAttempt() {
    }

}
