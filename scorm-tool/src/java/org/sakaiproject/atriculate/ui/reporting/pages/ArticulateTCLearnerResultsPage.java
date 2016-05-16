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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.sakaiproject.scorm.model.api.ActivitySummary;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.Learner;
import org.sakaiproject.scorm.ui.console.pages.DisplayDesignatedPackage;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.scorm.ui.reporting.util.SummaryProvider;
import org.sakaiproject.wicket.markup.html.link.BookmarkablePageLabeledLink;
import org.sakaiproject.wicket.markup.html.repeater.data.presenter.EnhancedDataPresenter;
import org.sakaiproject.wicket.markup.html.repeater.data.table.Action;
import org.sakaiproject.wicket.markup.html.repeater.data.table.ActionColumn;

public class ArticulateTCLearnerResultsPage extends ArticulateTCBaseResultsPage {

    private static final long serialVersionUID = 1L;

    private static final ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCLearnerResultsPage.class, "res/report_user.png");

    public ArticulateTCLearnerResultsPage(PageParameters pageParams) {
        super(pageParams);
    }

    @Override
    protected ResourceReference getPageIconReference() {
        return PAGE_ICON;
    }

    private List<IColumn> getColumns() {
        IModel titleHeader = new ResourceModel("column.header.title");
        IModel scoreHeader = new ResourceModel("column.header.score");
        IModel completedHeader = new ResourceModel("column.header.completed");
        IModel successHeader = new ResourceModel("column.header.success");

        List<IColumn> columns = new LinkedList<IColumn>();

        String[] paramPropertyExpressions = {"contentPackageId", "learnerId", "scoId", "attemptNumber"};

        columns.add(new PercentageColumn(scoreHeader, "scaled", "scaled"));

        columns.add(new PropertyColumn(completedHeader, "completionStatus", "completionStatus"));

        columns.add(new PropertyColumn(successHeader, "successStatus", "successStatus"));

        return columns;
    }

}
