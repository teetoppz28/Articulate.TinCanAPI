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

    int count(long contentPackageId, String learnerId);

    List<ArticulateTCAttempt> find(long contentPackageId);

    List<ArticulateTCAttempt> find(long contentPackageId, String learnerId);

    List<ArticulateTCAttempt> find(String courseId, String learnerId);

    ArticulateTCAttempt find(String courseId, String learnerId, long attemptNumber);

    ArticulateTCAttempt load(long id);

    ArticulateTCAttempt lookup(long contentPackageId, String learnerId, long attemptNumber);

    ArticulateTCAttempt lookupNewest(long contentPackageId, String learnerId);

    void save(ArticulateTCAttempt articulateTCAttempt);

}
