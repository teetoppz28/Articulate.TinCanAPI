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

import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCResultsService;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.atriculate.ui.reporting.providers.ArticulateTCLearnerResultsProvider;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCLearnerResultsPage extends ArticulateTCBaseResultsPage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    @SpringBean (name = "articulateTCResultsService")
    private ArticulateTCResultsService articulateTCResultsService;

    private static final ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCLearnerResultsPage.class, HTML_RES_REPORTING_PREFIX + "res/report_user.png");

    public ArticulateTCLearnerResultsPage(final PageParameters pageParameters) {
        super(pageParameters);

        final boolean isInstructor = StringUtils.equalsIgnoreCase(pageParameters.getString("isInstructor"), "true");
        String contentPackageId = pageParameters.getString("contentPackageId");

        Map<String, String> userData = articulateTCResultsService.calculateUserData(pageParameters.getString("userId"), pageParameters.getString("fullName"));
        String userId = userData.get(RESULTS_KEY_USERID);
        String fullName = userData.get(RESULTS_KEY_FULLNAME);

        Map<String, List<ArticulateTCAttemptResult>> results = articulateTCResultsService.calculateAttemptResults(contentPackageId, userId);
        List<ArticulateTCAttemptResult> articulateTCAttemptResultsComplete = results.get(RESULTS_KEY_COMPLETE);
        List<ArticulateTCAttemptResult> articulateTCAttemptResultsIncomplete = results.get(RESULTS_KEY_INCOMPLETE);

        String gradebookScore = articulateTCResultsService.calculateGradebookScore(contentPackageId, pageParameters.getString("gradebookScore"), userId);

        String gradebookPointsPossible = articulateTCResultsService.calculatePointsPossible(contentPackageId, pageParameters.getString("gradebookPointsPossible"));

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
        add(new Label("gradebook-score", (gradebookScore == null ? CONFIGURATION_GRADEBOOK_NO_POINTS : gradebookScore) + " / " + gradebookPointsPossible));

        add(new Link<Void>("link-back-results-page") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ArticulateTCResultsListPage.class, pageParameters);
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
                setResponsePage(PackageListPage.class, pageParameters);
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
