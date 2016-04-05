package org.sakaiproject.scorm.service.impl;

import org.sakaiproject.scorm.dao.LearnerDao;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.dao.api.ContentPackageManifestDao;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormResourceService;

public abstract class BaseContentServiceImpl {

    protected abstract LearnerDao learnerDao();
    protected abstract LearningManagementSystem lms();
    protected abstract ContentPackageDao contentPackageDao();
    protected abstract ContentPackageManifestDao contentPackageManifestDao();
    protected abstract ScormResourceService resourceService();

}
