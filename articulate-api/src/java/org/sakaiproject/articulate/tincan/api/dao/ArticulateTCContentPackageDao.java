/*
 * #%L
 * SCORM API
 * %%
 * Copyright (C) 2007 - 2016 Sakai Project
 * %%
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *             http://opensource.org/licenses/ecl2
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.sakaiproject.articulate.tincan.api.dao;

import java.util.List;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public interface ArticulateTCContentPackageDao {

    /**
     * Get the count of all content packages for a site with a specific name
     * 
     * @param context
     * @param name
     * @return
     */
    int countContentPackages(String context, String name);

    /**
     * Get all content packages for a site
     * 
     * @param context
     * @return
     */
    List<ArticulateTCContentPackage> find(String context);

    /**
     * Get a specific content package with ID
     * 
     * @param id
     * @return
     */
    ArticulateTCContentPackage get(long id);

    /**
     * Get a specific content package with ID (possibly cached)
     * 
     * @param id
     * @return
     */
    ArticulateTCContentPackage load(long id);

    /**
     * Softly delete a content package
     * 
     * @param articulateTContentPackage
     */
    void remove(ArticulateTCContentPackage articulateTContentPackage);

    /**
     * Save or update a content package object
     * 
     * @param articulateTContentPackage
     */
    void save(ArticulateTCContentPackage articulateTContentPackage);

}
