package org.sakaiproject.scorm.service.api;

import java.io.File;

import org.sakaiproject.scorm.model.api.TinCanAPIManifest;

public interface TinCanAPIContentService {

    /**
     * Creates a manifest object
     * 
     * @return the {@link TinCanAPIManifest} object
     */
    public TinCanAPIManifest createManifest(File file);

}
