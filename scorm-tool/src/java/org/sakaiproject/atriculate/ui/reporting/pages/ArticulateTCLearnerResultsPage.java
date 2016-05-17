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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.StringResourceModel;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.atriculate.ui.console.pages.ArticulateTCPackageListPage;
import org.sakaiproject.atriculate.ui.reporting.providers.ArticulateTCLearnerResultsProvider;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;

public class ArticulateTCLearnerResultsPage extends ArticulateTCBaseResultsPage {

    private static final long serialVersionUID = 1L;

    private static final ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCLearnerResultsPage.class, RES_REPORTING_PREFIX + "res/report_user.png");

    public ArticulateTCLearnerResultsPage(final PageParameters pageParams) {
        super(pageParams);

        List<ArticulateTCAttemptResult> articulateTCAttemptResultsComplete = new ArrayList<ArticulateTCAttemptResult>();
        List<ArticulateTCAttemptResult> articulateTCAttemptResultsIncomplete = new ArrayList<ArticulateTCAttemptResult>();

        final boolean isStudent;
        String contentPackageId = pageParams.getString("contentPackageId");

        String userId = null;
        String fullName = pageParams.getString("fullName");

        if (StringUtils.isBlank(fullName)) {
            // no full name passed in, must be from student page
            isStudent = true;

            User user = userDirectoryService.getCurrentUser();

            fullName = user != null ? user.getDisplayName() : "";
            userId = user != null ? user.getId() : "";
        } else {
            // is a maintainer
            isStudent = false;
        }

        String attemptId = pageParams.getString("attemptId");

        if (StringUtils.isBlank(attemptId)) {
            ArticulateTCAttempt articulateTCAttempt = articulateTCAttemptDao.lookupNewest(Long.parseLong(contentPackageId), userId);

            if (articulateTCAttempt != null) {
                attemptId = Long.toString(articulateTCAttempt.getId());
            }
        }

        String gradebookScore = pageParams.getString("gradebookScore");

        if (StringUtils.isBlank(gradebookScore)) {
            gradebookScore = "N/A";
            ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(Long.parseLong(contentPackageId));

            if (articulateTCContentPackage != null) {
                if (articulateTCContentPackage.getAssignmentId() != null) {
                    try {
                        developerHelperService.setCurrentUser(DeveloperHelperService.ADMIN_USER_REF);
                        gradebookScore = gradebookService.getAssignmentScoreString(articulateTCContentPackage.getContext(), articulateTCContentPackage.getAssignmentId(), userId);
                    } catch (Exception e) {
                    } finally {
                        developerHelperService.restoreCurrentUser();
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(attemptId)) {
            articulateTCAttemptResultsComplete = articulateTCAttemptResultDao.findByAttemptId(Long.parseLong(attemptId));
            articulateTCAttemptResultsIncomplete = articulateTCAttemptResultDao.findByAttemptIdIncomplete(Long.parseLong(attemptId));
        }

        List<IColumn<ArticulateTCAttemptResult>> completeColumns = new ArrayList<IColumn<ArticulateTCAttemptResult>>();
        completeColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.attempt.number", this, null), "attemptNumber", "attemptNumber"));
        completeColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.date.completed", this, null), "dateCompleted", "dateCompleted"));
        completeColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.scaled.score", this, null), "scaledScore", "scaledScore"));

        BasicDataTable resultsTableComplete = new BasicDataTable("results-table-complete", completeColumns, new ArticulateTCLearnerResultsProvider(articulateTCAttemptResultsComplete));
        add(resultsTableComplete);

        List<IColumn<ArticulateTCAttemptResult>> incompleteColumns = new ArrayList<IColumn<ArticulateTCAttemptResult>>();
        incompleteColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.attempt.number", this, null), "attemptNumber", "attemptNumber"));
        incompleteColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.date.begin", this, null), "modified", "modified"));
        incompleteColumns.add(new PropertyColumn<ArticulateTCAttemptResult>(new StringResourceModel("column.header.scaled.score", this, null), "scaledScore", "scaledScore"));

        BasicDataTable resultsTableIncomplete = new BasicDataTable("results-table-incomplete", incompleteColumns, new ArticulateTCLearnerResultsProvider(articulateTCAttemptResultsIncomplete));
        add(resultsTableIncomplete);

        add(new Label("full-name", fullName));
        add(new Label("gradebook-score", gradebookScore));

        add(new Link<Void>("link-back-results-page") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ArticulateTCResultsListPage.class, pageParams);
            }

            @Override
            public boolean isVisible() {
                return !isStudent;
            }
        });

        add(new Link<Void>("link-back-list-page") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ArticulateTCPackageListPage.class, pageParams);
            }

            @Override
            public boolean isVisible() {
                return isStudent;
            }
        });
    }

    @Override
    protected ResourceReference getPageIconReference() {
        return PAGE_ICON;
    }

}
