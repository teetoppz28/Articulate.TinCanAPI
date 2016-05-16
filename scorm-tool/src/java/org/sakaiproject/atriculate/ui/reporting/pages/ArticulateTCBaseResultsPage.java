/*
 * #%L
 * SCORM Tool
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
package org.sakaiproject.atriculate.ui.reporting.pages;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptResultDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.atriculate.ui.console.pages.ArticulateTCConsoleBasePage;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.wicket.markup.html.repeater.data.table.DecoratedPropertyColumn;

public abstract class ArticulateTCBaseResultsPage extends ArticulateTCConsoleBasePage {

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(ArticulateTCBaseResultsPage.class);

    private static final ResourceReference NEXT_ICON = new ResourceReference(ArticulateTCBaseResultsPage.class, RES_PREFIX + "res/arrow_right.png");
    private static final ResourceReference PREV_ICON = new ResourceReference(ArticulateTCBaseResultsPage.class, RES_PREFIX + "res/arrow_left.png");

    @SpringBean(name="articulateTCAttemptDao")
    protected ArticulateTCAttemptDao articulateTCAttemptDao;

    @SpringBean(name="articulateTCAttemptResultDao")
    protected ArticulateTCAttemptResultDao articulateTCAttemptResultDao;

    @SpringBean(name="articulateTCContentPackageDao")
    protected ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @SpringBean(name="org.sakaiproject.entitybroker.DeveloperHelperService")
    protected DeveloperHelperService developerHelperService;

    @SpringBean(name="org.sakaiproject.service.gradebook.GradebookService")
    protected GradebookService gradebookService;

    @SpringBean(name = "org.sakaiproject.site.api.SiteService")
    protected SiteService siteService;

    @SpringBean(name = "org.sakaiproject.user.api.UserDirectoryService")
    protected UserDirectoryService userDirectoryService;

    public ArticulateTCBaseResultsPage(PageParameters pageParams) {
        super(pageParams);

        long contentPackageId = pageParams.getLong("contentPackageId");
        String learnerId = developerHelperService.getCurrentUserId();

        ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(contentPackageId);

        List<ArticulateTCAttempt> attempts = articulateTCAttemptDao.find(contentPackageId, learnerId);
        int numberOfAttempts = attempts.size();
        long attemptNumber = 0;

    }

    public class PercentageColumn extends DecoratedPropertyColumn {

        private static final long serialVersionUID = 1L;

        public PercentageColumn(IModel displayModel, String sortProperty, String propertyExpression) {
            super(displayModel, sortProperty, propertyExpression);
        }

        @Override
        public Object convertObject(Object object) {
            Double d = (Double)object;
            
            return getPercentageString(d);
        }

        private String getPercentageString(double d) {
            double p = d * 100.0;
            String percentage = "" + p + " %";

            if (d < 0.0) {
                percentage = "Not available";
            }

            return percentage;
        }
    }

}
