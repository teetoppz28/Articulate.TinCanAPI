package org.sakaiproject.articulate.tincan.util;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCSecurityUtils {

    /**
     * Do the security checks
     * 
     * @return true, if all of the checks pass
     */
    public static void securityCheck() {
        if (!hasSession()) {
            throw new SecurityException("No valid session found");
        }
    }

    /**
     * Is this request in a valid session?
     * 
     * @return true, if there is a session associated with this current request
     */
    public static boolean hasSession() {
        SessionManager sessionManager = (SessionManager) ComponentManager.get(SessionManager.class);
        Session session = sessionManager.getCurrentSession();

        return session != null;
    }

    /**
     * Is the current user defined in the session allowed to access the given site?
     * 
     * @param siteId the site ID to check access
     * @return true, if the current user is allowed to access
     */
    public static boolean currentUserAllowedAccessToSite(String siteId) {
        SiteService siteService = (SiteService) ComponentManager.get(SiteService.class);

        return siteService.isCurrentUserMemberOfSite(siteId);
    }

    /**
     * Is the current user defined in the session a maintainer in the current site?
     * 
     * @param siteId the site ID to check access
     * @return true, if the current user is allowed to access
     */
    public static boolean isCurrentUserMaintainerInCurrentSite() {
        DeveloperHelperService developerHelperService = (DeveloperHelperService) ComponentManager.get(DeveloperHelperService.class);
        AuthzGroupService authzGroupService = (AuthzGroupService) ComponentManager.get(AuthzGroupService.class);

        String userRole = authzGroupService.getUserRole(developerHelperService.getCurrentUserId(), developerHelperService.getCurrentLocationReference());
        Set<String> maintainerRoles = authzGroupService.getMaintainRoles();

        for (String maintainerRole : maintainerRoles) {
            if (StringUtils.equalsIgnoreCase(userRole, maintainerRole)) {
                return true;
            }
        }

        return false;
    }

}
