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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageSettingsDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackageSettings;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.wicket.markup.html.form.CancelButton;
import org.sakaiproject.wicket.model.DecoratedPropertyModel;
import org.sakaiproject.wicket.model.SimpleDateFormatPropertyModel;

public class ArticulateTCPackageConfigurationPage extends ConsoleBasePage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    @SpringBean(name="org.sakaiproject.tool.api.ToolManager")
    private ToolManager toolManager;

    @SpringBean(name="articulateTCContentPackageSettingsDao")
    private ArticulateTCContentPackageSettingsDao articulateTCContentPackageSettingsDao;

    @SpringBean
    LearningManagementSystem lms;

    @SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
    ScormContentService contentService;

    @SpringBean(name = "org.sakaiproject.service.gradebook.GradebookExternalAssessmentService")
    GradebookExternalAssessmentService gradebookExternalAssessmentService;

    private String unlimitedMessage;
    private boolean hasGradebookInSite;
    private boolean hasGradebookItem;
    private static ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCPackageConfigurationPage.class, "res/table_edit.png");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ArticulateTCPackageConfigurationPage(final PageParameters params) {
        super(params);
        long contentPackageId = params.getLong("contentPackageId");

        final ContentPackage contentPackage = contentService.getContentPackage(contentPackageId);
        final ArticulateTCContentPackageSettings articulateTCContentPackageSettings = articulateTCContentPackageSettingsDao.findOneByPackageId(contentPackageId);

        Form<Object> form = new Form<Object>("configurationForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                boolean on = true;
                String assessmentExternalId = buildExternalId(contentPackage);
                String context = getContext();
                boolean has = gradebookExternalAssessmentService.isExternalAssignmentDefined(context, assessmentExternalId);
                String fixedTitle = getItemTitle(articulateTCContentPackageSettings, context);
                Double points = articulateTCContentPackageSettings.getPoints();

                if (has && on) {
                    gradebookExternalAssessmentService.updateExternalAssessment(context, assessmentExternalId, null, fixedTitle, points, contentPackage.getDueOn(), false);
                } else if (!has && on) {
                    gradebookExternalAssessmentService.addExternalAssessment(context, assessmentExternalId, null, fixedTitle, points, contentPackage.getDueOn(), CONFIGURATION_DEFAULT_GRADEBOOK_EXTERNAL_APP, false);
                } else if (has && !on) {
                    gradebookExternalAssessmentService.removeExternalAssessment(context, assessmentExternalId);
                }

                contentService.updateContentPackage(contentPackage);

                articulateTCContentPackageSettingsDao.save(articulateTCContentPackageSettings);

                if(params.getBoolean("no-toolbar")) {
                    setResponsePage(DisplayDesignatedPackage.class);
                } else {
                    setResponsePage(PackageListPage.class);
                }
            }
        };

        List<Integer> tryList = new LinkedList<Integer>();

        tryList.add(-1);
        for (int i = 1; i <= CONFIGURATION_DEFAULT_ATTEMPTS; i++) {
            tryList.add(i);
        }

        this.unlimitedMessage = getLocalizer().getString("unlimited", this);

        TextField<?> nameField = new TextField<ContentPackage>("packageName", new PropertyModel<ContentPackage>(contentPackage, "title"));
        nameField.setRequired(true);
        form.add(nameField);

        DateTimeField releaseOnDTF = new DateTimeField("releaseOnDTF", new PropertyModel(contentPackage, "releaseOn"));
        releaseOnDTF.setRequired(true);
        form.add(releaseOnDTF);
        form.add(new DateTimeField("dueOnDTF", new PropertyModel(contentPackage, "dueOn")));
        form.add(new DateTimeField("acceptUntilDTF", new PropertyModel(contentPackage, "acceptUntil")));
        form.add(new DropDownChoice<Object>("numberOfTries", new PropertyModel<Object>(contentPackage, "numberOfTries"), tryList, new TryChoiceRenderer()));
        form.add(new Label("createdBy", new DisplayNamePropertyModel(contentPackage, "createdBy")));
        form.add(new Label("createdOn", new SimpleDateFormatPropertyModel(contentPackage, "createdOn")));
        form.add(new Label("modifiedBy", new DisplayNamePropertyModel(contentPackage, "modifiedBy")));
        form.add(new Label("modifiedOn", new SimpleDateFormatPropertyModel(contentPackage, "modifiedOn")));

        hasGradebookInSite = gradebookExternalAssessmentService.isGradebookDefined(getContext());
        hasGradebookItem = gradebookExternalAssessmentService.isAssignmentDefined(getContext(), articulateTCContentPackageSettings.getGradebookItemTitle());

        final WebMarkupContainer gradebookSettingsVerifyMessageContainer = new WebMarkupContainer("gradebook-verify-message");
        gradebookSettingsVerifyMessageContainer.setOutputMarkupId(true);
        gradebookSettingsVerifyMessageContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsVerifyMessageContainer.setMarkupId("gradebook-verify-message");
        gradebookSettingsVerifyMessageContainer.setVisible(false);
        form.add(gradebookSettingsVerifyMessageContainer);

        final WebMarkupContainer gradebookSettingsCheckboxContainer = new WebMarkupContainer("gradebook-checkbox-sync");
        gradebookSettingsCheckboxContainer.setOutputMarkupId(true);
        gradebookSettingsCheckboxContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsCheckboxContainer.setMarkupId("gradebook-checkbox-sync");
        gradebookSettingsCheckboxContainer.setVisible(hasGradebookInSite);
        form.add(gradebookSettingsCheckboxContainer);

        final WebMarkupContainer gradebookSettingsTitleContainer = new WebMarkupContainer("gradebook-text-title");
        gradebookSettingsTitleContainer.setOutputMarkupId(true);
        gradebookSettingsTitleContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsTitleContainer.setMarkupId("gradebook-text-title");
        gradebookSettingsTitleContainer.setVisible(hasGradebookInSite && hasGradebookItem);
        form.add(gradebookSettingsTitleContainer);

        final TextField<?> gradebookSettingsTitle = new TextField<String>("gradebook-input-text-title", new PropertyModel<String>(articulateTCContentPackageSettings, "gradebookItemTitle"));
        gradebookSettingsTitle.setOutputMarkupId(true);
        gradebookSettingsTitle.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsTitle.setMarkupId("gradebook-input-text-title");
        gradebookSettingsTitleContainer.add(gradebookSettingsTitle);

        final WebMarkupContainer gradebookSettingsPointsContainer = new WebMarkupContainer("gradebook-text-points");
        gradebookSettingsPointsContainer.setOutputMarkupId(true);
        gradebookSettingsPointsContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsPointsContainer.setMarkupId("gradebook-text-points");
        gradebookSettingsPointsContainer.setVisible(hasGradebookInSite && hasGradebookItem);
        form.add(gradebookSettingsPointsContainer);

        final TextField<?> gradebookSettingsPoints = new TextField<Double>("gradebook-input-text-points", new PropertyModel<Double>(articulateTCContentPackageSettings, "points"));
        gradebookSettingsPoints.setOutputMarkupId(true);
        gradebookSettingsPoints.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsPoints.setMarkupId("gradebook-input-text-points");
        gradebookSettingsPointsContainer.add(gradebookSettingsPoints);

        AjaxCheckBox gradebookCheckboxSync = new AjaxCheckBox("gradebook-input-checkbox-sync", new PropertyModel<Boolean>(articulateTCContentPackageSettings, "graded")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isChecked = this.getConvertedInput();
                gradebookSettingsVerifyMessageContainer.setVisible(this.getModelObject() && !isChecked);
                target.addComponent(gradebookSettingsVerifyMessageContainer);
                gradebookSettingsTitleContainer.setVisible(isChecked);
                target.addComponent(gradebookSettingsTitleContainer);
                gradebookSettingsPointsContainer.setVisible(isChecked);
                target.addComponent(gradebookSettingsPointsContainer);
            }
        };
        gradebookCheckboxSync.setOutputMarkupId(true);
        gradebookCheckboxSync.setOutputMarkupPlaceholderTag(true);
        gradebookCheckboxSync.setMarkupId("gradebook-input-checkbox-sync");
        gradebookSettingsCheckboxContainer.add(gradebookCheckboxSync);

        form.add(new CancelButton("cancel", (params.getBoolean("no-toolbar")) ? DisplayDesignatedPackage.class : PackageListPage.class));

        add(form);
    }

    protected String getItemTitle(ArticulateTCContentPackageSettings articulateTCContentPackageSettings, String context) {
        String fixedTitle = articulateTCContentPackageSettings.getGradebookItemTitle();
        int count = 1;

        while (gradebookExternalAssessmentService.isAssignmentDefined(context, fixedTitle)) {
            fixedTitle = articulateTCContentPackageSettings.getGradebookItemTitle() + " (" + count++ + ")";
        }
  
        return fixedTitle;
    }

    protected String buildExternalId(ContentPackage contentPackage) {
        return getContext() + STATE_DATA_ID_SEPARATOR + contentPackage.getContentPackageId();
    }

    protected String getContext() {
        Placement placement = toolManager.getCurrentPlacement();
        String context = placement.getContext();

        return context;
    }

    @Override
    protected ResourceReference getPageIconReference() {
        return PAGE_ICON;
    }

    public class DisplayNamePropertyModel extends DecoratedPropertyModel implements Serializable {
        private static final long serialVersionUID = 1L;

        public DisplayNamePropertyModel(Object modelObject, String expression) {
            super(modelObject, expression);
        }

        @Override
        public Object convertObject(Object object) {
            String userId = String.valueOf(object);

            return lms.getLearnerName(userId);
        }
    }

    public class TryChoiceRenderer extends ChoiceRenderer<Object> implements Serializable {
        private static final long serialVersionUID = 1L;

        public TryChoiceRenderer() {
            super();
        }

        @Override
        public Object getDisplayValue(Object object) {
            Integer n = (Integer) object;

            if (n == -1) {
                return unlimitedMessage;
            }

            return object;
        }
    }

}
