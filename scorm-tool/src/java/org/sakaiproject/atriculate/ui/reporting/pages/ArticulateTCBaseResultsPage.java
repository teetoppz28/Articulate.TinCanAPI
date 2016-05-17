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

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptResultDao;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.atriculate.ui.console.pages.ArticulateTCConsoleBasePage;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;

public abstract class ArticulateTCBaseResultsPage extends ArticulateTCConsoleBasePage {

    private static final long serialVersionUID = 1L;

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
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CONSOLE_CSS);
        response.renderCSSReference(HTML_ARTICULATE_TC_CSS);
        response.renderCSSReference(HTML_BOOTSTRAP_CSS);
    }

}
