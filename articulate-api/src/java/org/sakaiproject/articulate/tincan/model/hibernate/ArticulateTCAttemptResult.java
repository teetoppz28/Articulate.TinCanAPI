package org.sakaiproject.articulate.tincan.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCAttemptResult implements ArticulateTCConstants, Serializable {

    private static final long serialVersionUID = 1L;

    @Setter @Getter private Long id;
    @Setter @Getter private Long attemptId;
    @Setter @Getter private Long attemptNumber;
    @Setter @Getter private Double scaledScore;
    @Setter @Getter private Date dateCompleted;
    @Setter @Getter private Date modified;

    public ArticulateTCAttemptResult() {
        this.scaledScore = CONFIGURATION_DEFAULT_SCALED_SCORE;
    }

}
