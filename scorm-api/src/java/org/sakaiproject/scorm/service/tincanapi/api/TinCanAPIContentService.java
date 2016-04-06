package org.sakaiproject.scorm.service.tincanapi.api;


import java.io.InputStream;

import org.sakaiproject.scorm.model.tincanapi.TinCanAPIManifest;

public interface TinCanAPIContentService {

    /**
     * Validates the file uploaded is a valid TinCanAPI zip archive and processes it
     * @param inputStream the uploaded content stream
     * @param packageName the name of the file uploaded
     * @param contentType the mimetype of the content
     * @return
     */
    public int validateAndProcess(InputStream inputStream, String packageName, String contentType);

    /**
     * Handles the directory creation and extraction of files from the zip archive
     * 
     * @param inputStream the uploaded content stream
     * @param packageName the name of the file uploaded
     * @param contentType the mimetype of the content
     * @return the launch URL
     */
    public String processPackage(InputStream inputStream, String packageName, String contentType);

    /**
     * Creates a manifest object
     * 
     * @return the {@link TinCanAPIManifest} object
     */
    public TinCanAPIManifest createManifest();

}
