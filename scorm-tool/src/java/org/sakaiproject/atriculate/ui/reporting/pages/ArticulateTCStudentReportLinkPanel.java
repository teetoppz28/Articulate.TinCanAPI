package org.sakaiproject.atriculate.ui.reporting.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMemberAttemptResult;

public class ArticulateTCStudentReportLinkPanel<T> extends PropertyColumn<T> {

    private static final long serialVersionUID = 1L;

    private final String property;
    private final PageParameters pageParameters;

    public ArticulateTCStudentReportLinkPanel(IModel<String> displayModel, String property) {
        this(displayModel, property, null);
    }

    public ArticulateTCStudentReportLinkPanel(IModel<String> displayModel, String property, String sort) {
        this(displayModel, sort, property, null);
    }

    public ArticulateTCStudentReportLinkPanel(IModel<String> displayModel, String property, String sort, PageParameters pageParameters) {
        super(displayModel, property, sort);
        this.property = property;
        this.pageParameters = pageParameters;
    }

    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(new LinkPanel(componentId, rowModel, new PropertyModel<Object>(rowModel, property)));
    }

    protected void onClick(IModel<T> clicked) {
        
    }

    private class LinkPanel extends Panel {

        private static final long serialVersionUID = 1L;

        public LinkPanel(String id, final IModel<T> rowModel, IModel<?> labelModel) {
            super(id);

            Link<T> link = new Link<T>("link", rowModel) {
                private static final long serialVersionUID = 1L;

                ArticulateTCMemberAttemptResult articulateTCMemberAttemptResult = (ArticulateTCMemberAttemptResult) rowModel.getObject();

                @Override
                public void onClick() {
                    pageParameters.remove("fullName");
                    pageParameters.remove("userId");
                    pageParameters.remove("gradebookScore");

                    pageParameters.add("fullName", articulateTCMemberAttemptResult.getFullName());
                    pageParameters.add("userId", articulateTCMemberAttemptResult.getUserId());
                    pageParameters.add("gradebookScore", articulateTCMemberAttemptResult.getGradebookScore());

                    setResponsePage(ArticulateTCLearnerResultsPage.class, pageParameters);
                }

                @Override
                public boolean isEnabled() {
                    return articulateTCMemberAttemptResult.getArticulateTCAttempt() != null;
                }
            };

            link.add(new Label("label", labelModel));

            add(link);
        }
    }

}
