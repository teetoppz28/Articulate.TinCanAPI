package org.sakaiproject.scorm.service.tincanapi.api;

import java.io.InputStream;

import org.sakaiproject.scorm.model.tincanapi.TinCanAPIContentPackage;
import org.sakaiproject.scorm.model.tincanapi.TinCanAPIMeta;

public interface TinCanAPIImporter {

    /**
     * Validates the file uploaded is a valid TinCanAPI zip archive and processes it
     *
     * @param inputStream the uploaded content stream
     * @param packageName the name of the file uploaded
     * @param contentType the mimetype of the content
     * @return the status
     */
    int validateAndProcess(InputStream inputStream, String packageName, String contentType);

    /**
     * Handles the directory creation and extraction of files from the zip archive
     *
     * @param inputStream the uploaded file stream
     * @param packageName the name of the file uploaded
     * @param contentType the mimetype of the content
     * @return true, if all processing was successful
     */
    boolean processPackage(InputStream inputStream, String packageName, String contentType);

    /**
     * Get the required data from the meta.xml file
     * 
     * @return the {@link TinCanAPIMeta} object, if valid
     */
    public TinCanAPIMeta processMetaXml();

    /**
     * Creates a content package object
     *
     * @return true, if the {@link TinCanAPIContentPackage} object is valid
     */
    boolean createContentPackage();

}
