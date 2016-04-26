package org.sakaiproject.articulate.tincan.api.dao;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackageSettings;

public interface ArticulateTCContentPackageSettingsDao {

    /**
     * Save or update (if the key exists) the object in the database
     * 
     * @param articulateTCContentPackageSettings
     */
    public void save(ArticulateTCContentPackageSettings articulateTCContentPackageSettings);

    /**
     * Get a row with the given ID
     * Use this one, preferably, over the load() method
     * 
     * @param id
     * @return
     */
    public ArticulateTCContentPackageSettings get(long id);

    /**
     * Get a row with the given ID
     * 
     * @param id
     * @return
     */
    public ArticulateTCContentPackageSettings load(long id);

    /**
     * Finds the row with the given package ID
     * 
     * @param packageId long version of the package ID
     * @return
     */
    public ArticulateTCContentPackageSettings findOneByPackageId(long packageId);

    /**
     * Finds the row with the given package ID
     * Convenience method
     * 
     * @param packageId string version of the package ID
     * @return
     */
    public ArticulateTCContentPackageSettings findOneByPackageId(String packageId);

    /**
     * Softly delete the data row in the db
     * 
     * @param id the id of the row
     */
    void remove(long id);

    /**
     * Is the given package ID an Articulate package?
     * 
     * @param packageId the ID of the content package
     * @return true, if a row exists
     */
    public boolean isArticulateContentPackage(long packageId);

}
