package org.sakaiproject.articulate.tincan.model;

import java.io.Serializable;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCMemberAttemptResult implements Serializable, ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    @Setter @Getter private String userId;
    @Setter @Getter private String eid;
    @Setter @Getter private String firstName;
    @Setter @Getter private String lastName;
    @Setter @Getter private String fullName;
    @Setter @Getter private String attemptNumber;
    @Setter @Getter private String gradebookPointsPossible;
    @Setter @Getter private String gradebookScore;

    public ArticulateTCMemberAttemptResult() {
    }

    public String getGradebookDisplay() {
        return (gradebookScore == null ? CONFIGURATION_GRADEBOOK_NO_POINTS : gradebookScore) + " / " + gradebookPointsPossible;
    }

}
