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
package org.sakaiproject.scorm.ui.console.pages;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.PropertyResolver;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageSettingsDao;
import org.sakaiproject.scorm.api.ScormConstants;
import org.sakaiproject.scorm.dao.api.AttemptDao;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.ui.console.components.DecoratedDatePropertyColumn;
import org.sakaiproject.scorm.ui.console.pages.articulate.ArticulateTCPackageConfigurationPage;
import org.sakaiproject.scorm.ui.console.pages.articulate.ArticulateTCPackageRemovePage;
import org.sakaiproject.scorm.ui.player.pages.PlayerPage;
import org.sakaiproject.scorm.ui.player.pages.articulate.ArticulateTCPlayerPage;
import org.sakaiproject.scorm.ui.reporting.pages.LearnerResultsPage;
import org.sakaiproject.scorm.ui.reporting.pages.ResultsListPage;
import org.sakaiproject.wicket.markup.html.link.BookmarkablePageLabeledLink;
import org.sakaiproject.wicket.markup.html.link.IconLink;
import org.sakaiproject.wicket.markup.html.repeater.data.table.Action;
import org.sakaiproject.wicket.markup.html.repeater.data.table.ActionColumn;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;
import org.sakaiproject.wicket.markup.html.repeater.data.table.ImageLinkColumn;

public class PackageListPage extends ConsoleBasePage implements ScormConstants {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(PackageListPage.class);

	private static final ResourceReference PAGE_ICON = new ResourceReference(PackageListPage.class, "res/table.png");
	private static final ResourceReference DELETE_ICON = new ResourceReference(PackageListPage.class, "res/delete.png");

    @SpringBean(name="articulateTCContentPackageSettingsDao")
    private ArticulateTCContentPackageSettingsDao articulateTCContentPackageSettingsDao;

    @SpringBean(name="org.sakaiproject.scorm.dao.api.AttemptDao")
    private AttemptDao attemptDao;

	@SpringBean
	LearningManagementSystem lms;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;

	@SuppressWarnings("unchecked")
    public PackageListPage(PageParameters params) {

		List<ContentPackage> contentPackages = contentService.getContentPackages();

		final String context = lms.currentContext();
		final boolean canConfigure = lms.canConfigure(context);
		final boolean canGrade = lms.canGrade(context);
		final boolean canViewResults = lms.canViewResults(context);
		final boolean canDelete = lms.canDelete(context);

		List<IColumn<ContentPackage>> columns = new LinkedList<IColumn<ContentPackage>>();

		ActionColumn actionColumn = new ActionColumn(new StringResourceModel("column.header.content.package.name", this, null), "title", "title");

		String[] paramPropertyExpressions = {"contentPackageId", "resourceId", "title", "url"};

		Action launchAction = new Action("title", paramPropertyExpressions){
			private static final long serialVersionUID = 1L;
			@Override
			public Component newLink(String id, Object bean) {
                ContentPackage contentPackage = (ContentPackage) bean;
                boolean isArticulate = articulateTCContentPackageSettingsDao.isArticulateContentPackage(contentPackage.getContentPackageId());
                pageClass = (isArticulate) ? ArticulateTCPlayerPage.class : PlayerPage.class;

                if (lms.canLaunchNewWindow()) {
                    setPopupWindowName("ScormPlayer");
                }

				IModel<String> labelModel;
				if (displayModel != null) {
					labelModel = displayModel;
				} else {
					String labelValue = String.valueOf(PropertyResolver.getValue(labelPropertyExpression, bean));
					labelModel = new Model<String>(labelValue);
				}

				PageParameters params = buildPageParameters(paramPropertyExpressions, bean);
				Link link = new BookmarkablePageLabeledLink(id, labelModel, pageClass, params);

				if (popupWindowName != null && !isArticulate) {
					PopupSettings popupSettings = new PopupSettings(PageMap.forName(popupWindowName), PopupSettings.RESIZABLE);
					popupSettings.setWidth(1020);
					popupSettings.setHeight(740);

					popupSettings.setWindowName(popupWindowName);

					link.setPopupSettings(popupSettings);
				}

				link.setEnabled(isEnabled(bean) && lms.canLaunch(contentPackage));
				link.setVisible(isVisible(bean));

				return link;
			}
		};

        actionColumn.addAction(launchAction);

        if (canConfigure) {
            actionColumn.addAction(
                new Action(new ResourceModel("column.action.edit.label"), paramPropertyExpressions) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public Component newLink(String id, Object bean) {
                        ContentPackage contentPackage = (ContentPackage) bean;
                        boolean isArticulate = articulateTCContentPackageSettingsDao.isArticulateContentPackage(contentPackage.getContentPackageId());
                        pageClass = (isArticulate) ? ArticulateTCPackageConfigurationPage.class : PackageConfigurationPage.class;
                        PageParameters params = buildPageParameters(paramPropertyExpressions, bean);
                        Link link = new BookmarkablePageLabeledLink(id, new ResourceModel("column.action.edit.label"), pageClass, params);

                        return link;
                    }
                }
            );
        }

        if (canGrade) {
            actionColumn.addAction(new Action(new StringResourceModel("column.action.grade.label", this, null), ResultsListPage.class, paramPropertyExpressions));
        }
        else if (canViewResults) {
            actionColumn.addAction(new Action(new StringResourceModel("column.action.grade.label", this, null), LearnerResultsPage.class, paramPropertyExpressions));
        }

		columns.add(actionColumn);

		columns.add(new StatusColumn(new StringResourceModel("column.header.status", this, null), "status"));

		columns.add(new DecoratedDatePropertyColumn(new StringResourceModel("column.header.releaseOn", this, null), "releaseOn", "releaseOn"));

		columns.add(new DecoratedDatePropertyColumn(new StringResourceModel("column.header.dueOn", this, null), "dueOn", "dueOn"));

        if (!canConfigure) {
            columns.add(new TriesColumn(new StringResourceModel("column.header.attempts", this, null), "numberOfTries"));
        }

        if (canConfigure) {
            columns.add(new TypeColumn(new StringResourceModel("column.header.type", this, null), "type"));
        }

        if (canDelete) {
            ImageLinkColumn link = new ImageLinkColumn(new Model("Remove"), PackageRemovePage.class, paramPropertyExpressions, DELETE_ICON, "delete") {
                private static final long serialVersionUID = 1L;

                @Override
                public void populateItem(Item cellItem, String componentId, IModel model) {
                    Object bean = model.getObject();

                    final PageParameters params = buildPageParameters(paramPropertyExpressions, bean);

                    ContentPackage contentPackage = (ContentPackage) bean;
                    boolean isArticulate = articulateTCContentPackageSettingsDao.isArticulateContentPackage(contentPackage.getContentPackageId());
                    Class<?> pageClass = (isArticulate) ? ArticulateTCPackageRemovePage.class : PackageRemovePage.class;

                    if (iconProperty != null) {
                        String iconPropertyValue = String.valueOf(PropertyResolver.getValue(iconProperty, bean));
                        iconReference = getIconPropertyReference(iconPropertyValue);
                    }

                    cellItem.add(new IconLink("cell", pageClass, params, iconReference, popupWindowName));
                }
            };
            columns.add(link);
        }

		BasicDataTable table = new BasicDataTable("cpTable", columns, contentPackages);

		add(table);
	}

	public class StatusColumn extends AbstractColumn<ContentPackage> {

		private static final long serialVersionUID = 1L;

		public StatusColumn(IModel<String> displayModel, String sortProperty) {
			super(displayModel, sortProperty);
		}

		public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
			item.add(new Label(componentId, createLabelModel(model)));
		}

		protected IModel<String> createLabelModel(IModel<ContentPackage> embeddedModel)
		{
			String resourceId = "status.unknown";
			Object target = embeddedModel.getObject();

			if (target instanceof ContentPackage) {
				ContentPackage contentPackage = (ContentPackage)target;

				int status = contentService.getContentPackageStatus(contentPackage);

				switch (status) {
				case CONTENT_PACKAGE_STATUS_OPEN:
					resourceId = "status.open";
					break;
				case CONTENT_PACKAGE_STATUS_OVERDUE:
					resourceId = "status.overdue";
					break;
				case CONTENT_PACKAGE_STATUS_CLOSED:
					resourceId = "status.closed";
					break;
				case CONTENT_PACKAGE_STATUS_NOTYETOPEN:
					resourceId = "status.notyetopen";
					break;
				}
			}

			return new ResourceModel(resourceId);
		}
	}

    public class TriesColumn extends AbstractColumn<ContentPackage> {

        private static final long serialVersionUID = 1L;

        public TriesColumn(IModel<String> displayModel, String sortProperty) {
            super(displayModel, sortProperty);
        }

        public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
            item.add(new Label(componentId, createLabelModel(model)));
        }

        protected String createLabelModel(IModel<ContentPackage> embeddedModel) {
            Object target = embeddedModel.getObject();

            if (target instanceof ContentPackage) {
                ContentPackage contentPackage = (ContentPackage) target;

                String userId = lms.currentLearnerId();
                int attemptsCount = attemptDao.count(contentPackage.getContentPackageId(), userId);
                int maxAttempts = contentPackage.getNumberOfTries();
                String tries = Integer.toString(attemptsCount) + " / ";

                if (maxAttempts == -1) {
                    return tries + "unlimited";
                }

                return  tries + Integer.toString(maxAttempts);
            }

            return "";
        }
    }

    public class TypeColumn extends AbstractColumn<ContentPackage> {

        private static final long serialVersionUID = 1L;

        public TypeColumn(IModel<String> displayModel, String sortProperty) {
            super(displayModel, sortProperty);
        }

        public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
            item.add(new Label(componentId, createLabelModel(model)));
        }

        protected String createLabelModel(IModel<ContentPackage> embeddedModel) {
            Object target = embeddedModel.getObject();

            if (target instanceof ContentPackage) {
                ContentPackage contentPackage = (ContentPackage) target;

                if (articulateTCContentPackageSettingsDao.isArticulateContentPackage(contentPackage.getContentPackageId())) {
                    return "Articulate TinCanAPI";
                } else {
                    return "SCORM 2004 v3";
                }
            }

            return "unknown";
        }
    }

	@Override
	protected ResourceReference getPageIconReference() {
		return PAGE_ICON;
	}

}
