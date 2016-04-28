package org.sakaiproject.articulate.tincan.api;

public interface ArticulateTCDeleteService {

    /**
     * Softly deletes the content package
     * 
     * @param contentPackageId
     * @return true, if successful
     */
    boolean deleteContentPackage(Long contentPackageId);

    /**
     * Softly delete the gradebook item
     * 
     * @param contentPackageId
     * @return true, if successful
     */
    boolean deleteGradebookItem(Long contentPackageId);

    /**
     * Deletes the resource files from the filesystem and admin resources /private directory
     * 
     * @param contentPackageId
     * @return true, if successful
     */
    boolean deleteResourceFiles(Long contentPackageId);

}
