package org.sakaiproject.articulate.tincan;

import org.sakaiproject.scorm.api.ScormConstants;

public interface ArticulateTCConstants extends ScormConstants {

    /**
     * Entity provider prefix
     */
    public final static String REST_PREFIX = "tincanapi-lrs";

    public final static String STATEMENT_PAYLOAD_CONTENT_VARIABLE = "content=";
    public final static String PATH_ACTION = "action";
    public final static String PATH_STATEMENTS = "statements";
    public final static String PATH_ACTIVITIES = "activities";
    public final static String PATH_STATE = "state";

    public final static String DEFAULT_ENCODING = "UTF-8";

    public final static String ARCHIVE_DEFAULT_PATH_PREFIX = "/access/content";
    public static String ARCHIVE_DEFAULT_PACKAGE_ROOT_NAME = "Articulate_TinCanAPI_Packages";
    public static String ARCHIVE_DEFAULT_LAUNCH_PAGE = "story.html";
    public static boolean ARCHIVE_DEFAULT_HIDE_ROOT_DIRECTORY = true;
    public final static String ARCHIVE_ZIP_MIMETYPE = "application/zip";
    public final static String ARCHIVE_META_XML_FILE = "meta.xml";
    public final static String ARCHIVE_TINCAN_XML_FILE = "tincan.xml";

    public static final String HTML_TINCANAPI_CSS = "styles/articulatetc.css";
    public static final String HTML_HEADSCRIPTS = "/library/js/headscripts.js";
    public static final String HTML_BODY_ONLOAD_ADDTL="setMainFrameHeight(window.name)";

}
