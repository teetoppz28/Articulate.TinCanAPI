package org.sakaiproject.scorm.entity.tincanapi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestAware;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestGetter;

public class TinCanAPIEntityProvider implements AutoRegisterEntityProvider, ActionsExecutable, RequestAware {
    public static String PREFIX = "lrs";
    private RequestGetter requestGetter;

    public String getEntityPrefix() {
        return PREFIX;
    }

    public Object getSampleEntity() {
        return PREFIX;
    }

    public String[] getHandledOutputFormats() {
        return Formats.ALL_KNOWN_FORMATS;
    }

    public String[] getHandledInputFormats() {
        return Formats.ALL_KNOWN_FORMATS;
    }

    @EntityCustomAction(action = "", viewKey = "")
    public ActionReturn root(EntityView view, Map<String, Object> params) {
        Map<String,Object> map = new HashMap<String, Object>();
        wtf(params);
        return new ActionReturn(map);
    }

    @EntityCustomAction(action = "statements", viewKey = "")
    public ActionReturn activityStatements(EntityView view, Map<String, Object> params) {
        Map<String,Object> map = new HashMap<String, Object>();
        wtf(params);
        return new ActionReturn(map);
    }

    @EntityCustomAction(action = "activities/{state}", viewKey = "")
    public ActionReturn activitiesState(EntityView view, Map<String, Object> params) {
        Map<String,Object> map = new HashMap<String, Object>();
        wtf(params);
        return new ActionReturn(map);
    }

    private void wtf(Map<String, Object> params) {
        Iterator i = params.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry pair = (Map.Entry)i.next();
            System.out.println("key: " + pair.getKey() + ", value: " + pair.getValue());
        }
    }

    @Override
    public void setRequestGetter(RequestGetter requestGetter) {
        this.requestGetter = requestGetter;
    }

}
