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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.api.ArticulateTCResultsService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMemberAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.atriculate.ui.reporting.providers.ArticulateTCResultsListProvider;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCResultsListPage extends ArticulateTCBaseResultsPage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name="articulateTCContentPackageDao")
    private ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @SpringBean (name = "articulateTCResultsService")
    private ArticulateTCResultsService articulateTCResultsService;

    private static final ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCLearnerResultsPage.class, HTML_RES_CONSOLE_PREFIX + "res/report.png");

    public ArticulateTCResultsListPage(final PageParameters pageParams) {
        super(pageParams);

        long contentPackageId = pageParams.getLong("contentPackageId");
        ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(contentPackageId);
        Long assignmentId = articulateTCContentPackage.getAssignmentId();
        String siteId = articulateTCContentPackage.getContext();

        // get attempts for each student
        List<ArticulateTCMemberAttemptResult> articulateTCMemberAttemptResults = articulateTCResultsService.calculateLearnerAttemptResults(siteId, contentPackageId, assignmentId);

        List<IColumn<ArticulateTCMemberAttemptResult>> columns = new ArrayList<>();
        columns.add(new ArticulateTCStudentReportLinkPanel<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.name", this, null), "fullName", "fullName", pageParams));
        columns.add(new PropertyColumn<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.id", this, null), "eid", "eid"));
        columns.add(new PropertyColumn<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.attempts", this, null), "attemptNumber", "attemptNumber"));
        columns.add(new PropertyColumn<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.gradebook.score", this, null), "gradebookScore", "gradebookDisplay"));

        BasicDataTable dataTable = new BasicDataTable("results-table", columns, new ArticulateTCResultsListProvider(articulateTCMemberAttemptResults));
        add(dataTable);

        add(new Link<Void>("link-back-list-page") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(PackageListPage.class, pageParams);
            }

            @Override
            public boolean isVisible() {
                return true;
            }
        });
    }

    @Override
    protected ResourceReference getPageIconReference() {
        return PAGE_ICON;
    }

}
