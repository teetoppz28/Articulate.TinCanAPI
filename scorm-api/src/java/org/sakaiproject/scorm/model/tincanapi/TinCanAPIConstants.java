package org.sakaiproject.scorm.model.tincanapi;

import org.sakaiproject.scorm.api.ScormConstants;

public interface TinCanAPIConstants extends ScormConstants {

    /**
     * Entity provider prefix
     */
    public final static String PREFIX = "tincanapi-lrs";

    public final static String PAYLOAD_CONTENT_VARIABLE = "content=";
    public final static String PATH_ACTION = "action";
    public final static String PATH_STATEMENTS = "statements";
    public final static String PATH_ACTIVITIES = "activities";
    public final static String PATH_STATE = "state";

    public final static String DEFAULT_ENCODING = "UTF-8";

    public final static String ZIP_MIMETYPE = "application/zip";
    public final static String META_XML_FILE = "meta.xml";
    public final static String TINCAN_XML_FILE = "tincan.xml";

}
