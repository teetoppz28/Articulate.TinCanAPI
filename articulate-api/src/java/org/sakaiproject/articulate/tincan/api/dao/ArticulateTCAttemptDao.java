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

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public interface ArticulateTCAttemptDao {

    /**
     * Get the number of attempts by user for the content package
     * 
     * @param contentPackageId
     * @param learnerId
     * @return
     */
    int count(long contentPackageId, String learnerId);

    /**
     * Find attempts by content package
     * 
     * @param contentPackageId
     * @return
     */
    List<ArticulateTCAttempt> find(long contentPackageId);

    /**
     * Find attempts by user for a content package
     * 
     * @param contentPackageId
     * @param learnerId
     * @return
     */
    List<ArticulateTCAttempt> find(long contentPackageId, String learnerId);

    /**
     * Find attempts by user for all content packages in a site
     * 
     * @param courseId
     * @param learnerId
     * @return
     */
    List<ArticulateTCAttempt> find(String courseId, String learnerId);

    /**
     * Find a specific attempt by user for a content package in a site
     * 
     * @param courseId
     * @param learnerId
     * @param attemptNumber
     * @return
     */
    ArticulateTCAttempt find(String courseId, String learnerId, long attemptNumber);

    /**
     * Get an attempt by ID
     * 
     * @param id
     * @return
     */
    ArticulateTCAttempt load(long id);

    /**
     * Find a specific attempt by user for a specific content package
     * 
     * @param courseId
     * @param learnerId
     * @param attemptNumber
     * @return
     */
    ArticulateTCAttempt lookup(long contentPackageId, String learnerId, long attemptNumber);

    /**
     * Find the latest attempt by a user for a content package
     * 
     * @param contentPackageId
     * @param learnerId
     * @return
     */
    ArticulateTCAttempt lookupNewest(long contentPackageId, String learnerId);

    /**
     * Save or update an attempt object
     * 
     * @param articulateTCAttempt
     */
    void save(ArticulateTCAttempt articulateTCAttempt);

}
