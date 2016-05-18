package org.sakaiproject.articulate.tincan.util;

import org.sakaiproject.component.cover.ComponentManager;
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

}
