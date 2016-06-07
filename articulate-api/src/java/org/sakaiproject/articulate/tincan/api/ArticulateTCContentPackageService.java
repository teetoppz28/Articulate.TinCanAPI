package org.sakaiproject.articulate.tincan.api;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;

public interface ArticulateTCContentPackageService {

    /**
     * Calculates the status of the content package
     * 
     * @param articulateTCContentPackage
     * @return
     */
    int getContentPackageStatus(ArticulateTCContentPackage articulateTCContentPackage);

    /**
     * Should the launch link be enabled?
     * 
     * @param articulateTCContentPackage
     * @return
     */
    boolean isEnabled(ArticulateTCContentPackage articulateTCContentPackage);

}
