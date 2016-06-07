package org.sakaiproject.articulate.tincan.impl;

import java.util.Date;
import java.util.Set;

import lombok.Setter;

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCContentPackageService;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.entitybroker.DeveloperHelperService;

public class ArticulateTCContentPackageServiceImpl implements ArticulateTCContentPackageService, ArticulateTCConstants {

    @Setter
    private AuthzGroupService authzGroupService;

    @Setter
    private DeveloperHelperService developerHelperService;

    @Override
    public int getContentPackageStatus(ArticulateTCContentPackage articulateTCContentPackage) {
        int status = CONTENT_PACKAGE_STATUS_UNKNOWN;
        Date now = new Date();

        if (now.after(articulateTCContentPackage.getReleaseOn())) {
            if (articulateTCContentPackage.getDueOn() == null || articulateTCContentPackage.getAcceptUntil() == null) {
                status = CONTENT_PACKAGE_STATUS_OPEN;
            } else if (now.before(articulateTCContentPackage.getDueOn())) {
                status = CONTENT_PACKAGE_STATUS_OPEN;
            } else if (now.before(articulateTCContentPackage.getAcceptUntil())) {
                status = CONTENT_PACKAGE_STATUS_OVERDUE;
            } else {
                status = CONTENT_PACKAGE_STATUS_CLOSED;
            }
        } else {
            status = CONTENT_PACKAGE_STATUS_NOTYETOPEN;
        }

        return status;
    }

    @Override
    public boolean isEnabled(ArticulateTCContentPackage articulateTCContentPackage) {
        String userId = developerHelperService.getCurrentUserId();
        String siteRef = developerHelperService.getCurrentLocationReference();
        String userRole = authzGroupService.getUserRole(userId, siteRef);
        Set<String> maintainerRoles = authzGroupService.getMaintainRoles();

        if (maintainerRoles.contains(userRole)) {
            return true;
        }

        int status = getContentPackageStatus(articulateTCContentPackage);

        if (status == CONTENT_PACKAGE_STATUS_OPEN) {
            return true;
        }

        if (status == CONTENT_PACKAGE_STATUS_OVERDUE) {
            return true;
        }

        return false;
    }

}
