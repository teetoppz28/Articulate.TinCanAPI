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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.api.ArticulateTCResultsService;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.atriculate.ui.reporting.providers.ArticulateTCLearnerResultsProvider;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCLearnerResultsPage extends ArticulateTCBaseResultsPage {

    private static final long serialVersionUID = 1L;

    @SpringBean (name = "articulateTCResultsService")
    private ArticulateTCResultsService articulateTCResultsService;

    @SpringBean(name = "org.sakaiproject.user.api.UserDirectoryService")
    private UserDirectoryService userDirectoryService;

    private static final ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCLearnerResultsPage.class, HTML_RES_REPORTING_PREFIX + "res/report_user.png");

    public ArticulateTCLearnerResultsPage(final PageParameters pageParams) {
        super(pageParams);

        final boolean isInstructor = StringUtils.equalsIgnoreCase(pageParams.getString("isInstructor"), "true");
        String contentPackageId = pageParams.getString("contentPackageId");

        String userId = pageParams.getString("userId");
        String fullName = pageParams.getString("fullName");

        if (StringUtils.isBlank(fullName) || StringUtils.isBlank(userId)) {
            // no full name or user ID passed in, must be from student page
            User user = userDirectoryService.getCurrentUser();

            fullName = user != null ? user.getDisplayName() : "";
            userId = user != null ? user.getId() : "";
        }

        Map<String, List<ArticulateTCAttemptResult>> results = articulateTCResultsService.calculateAttemptResults(contentPackageId, userId);
        List<ArticulateTCAttemptResult> articulateTCAttemptResultsComplete = results.get("complete");
        List<ArticulateTCAttemptResult> articulateTCAttemptResultsIncomplete = results.get("incomplete");

        String gradebookScore = articulateTCResultsService.calculateGradebookScore(contentPackageId, pageParams.getString("gradebookScore"), userId);

        String gradebookPointsPossible = articulateTCResultsService.calculatePointsPossible(contentPackageId, pageParams.getString("gradebookPointsPossible"));

        List<IColumn<ArticulateTCAttemptResult>> completeColumns = new ArrayList<>();
        completeColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.attempt.number", this, null), "attemptNumber", "attemptNumber"));
        completeColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.date.completed", this, null), "dateCompleted", "dateCompleted"));
        completeColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.scaled.score", this, null), "scaledScore", "scaledScore"));

        BasicDataTable resultsTableComplete = new BasicDataTable("results-table-complete", completeColumns, new ArticulateTCLearnerResultsProvider(articulateTCAttemptResultsComplete));
        add(resultsTableComplete);

        List<IColumn<ArticulateTCAttemptResult>> incompleteColumns = new ArrayList<>();
        incompleteColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.attempt.number", this, null), "attemptNumber", "attemptNumber"));
        incompleteColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.date.begin", this, null), "modified", "modified"));
        incompleteColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.scaled.score", this, null), "scaledScore", "scaledScore"));

        BasicDataTable resultsTableIncomplete = new BasicDataTable("results-table-incomplete", incompleteColumns, new ArticulateTCLearnerResultsProvider(articulateTCAttemptResultsIncomplete));
        add(resultsTableIncomplete);

        add(new Label("full-name", fullName));
        add(new Label("gradebook-score", (gradebookScore == null ? "-" : gradebookScore) + " / " + gradebookPointsPossible));

        add(new Link<Void>("link-back-results-page") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ArticulateTCResultsListPage.class, pageParams);
            }

            @Override
            public boolean isVisible() {
                return isInstructor;
            }
        });

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
