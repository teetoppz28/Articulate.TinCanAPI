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
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;
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
    private LearningManagementSystem lms;

    @SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
    private ScormContentService contentService;

    @SpringBean(name="org.sakaiproject.service.gradebook.GradebookService")
    private GradebookService gradebookService;

    @SpringBean(name="org.sakaiproject.entitybroker.DeveloperHelperService")
    private DeveloperHelperService developerHelperService;

    private String unlimitedMessage;
    private boolean hasGradebookInSite = false;
    private boolean hasGradebookItem = false;
    private static ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCPackageConfigurationPage.class, "res/table_edit.png");

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ArticulateTCPackageConfigurationPage(final PageParameters params) {
        super(params);
        long contentPackageId = params.getLong("contentPackageId");

        final ContentPackage contentPackage = contentService.getContentPackage(contentPackageId);
        final ArticulateTCContentPackageSettings articulateTCContentPackageSettings = articulateTCContentPackageSettingsDao.findOneByPackageId(contentPackageId);

        Form<Object> form = new Form<Object>("configurationForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                Assignment assignment = null;
                if (articulateTCContentPackageSettings.getGradebookItemId() != null) {
                    assignment = gradebookService.getAssignment(getContext(), articulateTCContentPackageSettings.getGradebookItemId());
                }

                boolean hasAssignmentDefined = assignment != null;
                boolean gradebookChecked = articulateTCContentPackageSettings.isGraded();
                String fixedTitle = getItemTitle(articulateTCContentPackageSettings, getContext());
                Double points = articulateTCContentPackageSettings.getPoints();

                if (hasAssignmentDefined && gradebookChecked) {
                    assignment.setDueDate(contentPackage.getDueOn());
                    assignment.setPoints(points);
                    gradebookService.updateAssignment(getContext(), assignment.getName(), assignment);
                } else if (!hasAssignmentDefined && gradebookChecked) {
                    assignment = new Assignment();
                    assignment.setName(fixedTitle);
                    assignment.setDueDate(contentPackage.getDueOn());
                    assignment.setPoints(points);
                    assignment.setCounted(true);
                    gradebookService.addAssignment(getContext(), assignment);
                    // sync the assignment IDs
                    assignment = gradebookService.getAssignment(getContext(), assignment.getName());
                    articulateTCContentPackageSettings.setGradebookItemId(assignment.getId());
                } else if (hasAssignmentDefined && !gradebookChecked) {
                    gradebookService.removeAssignment(assignment.getId());
                    // reset gradebook item title to package title
                    articulateTCContentPackageSettings.setGradebookItemTitle(contentPackage.getTitle());
                    // reset gradebook item points to default
                    articulateTCContentPackageSettings.setPoints(CONFIGURATION_DEFAULT_POINTS);
                    // reset the assignment ID
                    articulateTCContentPackageSettings.setGradebookItemId(null);
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

        hasGradebookInSite = gradebookService.isGradebookDefined(getContext());

        // get the current gradebook item, if it exists
        Assignment assignment = null;
        if (hasGradebookInSite && articulateTCContentPackageSettings.getGradebookItemId() != null) {
            assignment = gradebookService.getAssignment(getContext(), articulateTCContentPackageSettings.getGradebookItemId());
            hasGradebookItem = assignment != null;
        }

        // set if this item is already in the gradebook
        articulateTCContentPackageSettings.setGraded(hasGradebookItem);
        // set default gb title to content package
        articulateTCContentPackageSettings.setGradebookItemTitle(hasGradebookItem ? assignment.getName() : contentPackage.getTitle());
        // set default points
        articulateTCContentPackageSettings.setPoints(hasGradebookItem ? assignment.getPoints() : CONFIGURATION_DEFAULT_POINTS);

        /**
         * Verification message
         */
        final WebMarkupContainer gradebookSettingsVerifyMessageContainer = new WebMarkupContainer("gradebook-verify-message");
        gradebookSettingsVerifyMessageContainer.setOutputMarkupId(true);
        gradebookSettingsVerifyMessageContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsVerifyMessageContainer.setMarkupId("gradebook-verify-message");
        gradebookSettingsVerifyMessageContainer.setVisible(false);
        form.add(gradebookSettingsVerifyMessageContainer);

        /**
         * GB Sync checkbox container
         */
        final WebMarkupContainer gradebookSettingsCheckboxContainer = new WebMarkupContainer("gradebook-checkbox-sync");
        gradebookSettingsCheckboxContainer.setOutputMarkupId(true);
        gradebookSettingsCheckboxContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsCheckboxContainer.setMarkupId("gradebook-checkbox-sync");
        gradebookSettingsCheckboxContainer.setVisible(hasGradebookInSite);
        form.add(gradebookSettingsCheckboxContainer);

        /**
         * GB Title container
         */
        final WebMarkupContainer gradebookSettingsTitleContainer = new WebMarkupContainer("gradebook-text-title");
        gradebookSettingsTitleContainer.setOutputMarkupId(true);
        gradebookSettingsTitleContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsTitleContainer.setMarkupId("gradebook-text-title");
        gradebookSettingsTitleContainer.setVisible(hasGradebookInSite && hasGradebookItem);
        form.add(gradebookSettingsTitleContainer);

        /**
         * GB Title input
         */
        final TextField<?> gradebookSettingsTitle = new TextField<String>("gradebook-input-text-title", new PropertyModel<String>(articulateTCContentPackageSettings, "gradebookItemTitle"));
        gradebookSettingsTitle.setOutputMarkupId(true);
        gradebookSettingsTitle.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsTitle.setMarkupId("gradebook-input-text-title");
        gradebookSettingsTitle.setVisible(!hasGradebookItem); // only editable on first load
        gradebookSettingsTitleContainer.add(gradebookSettingsTitle);

        /*
         * GB Title non-editable text
         */
        final Label gradebookSettingsTitleLabel = new Label("gradebook-input-text-title-label", new PropertyModel<String>(articulateTCContentPackageSettings, "gradebookItemTitle"));
        gradebookSettingsTitleLabel.setOutputMarkupId(true);
        gradebookSettingsTitleLabel.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsTitleLabel.setMarkupId("gradebook-input-text-title-label");
        gradebookSettingsTitleLabel.setVisible(hasGradebookItem);
        gradebookSettingsTitleContainer.add(gradebookSettingsTitleLabel);

        /**
         * GB Points container
         */
        final WebMarkupContainer gradebookSettingsPointsContainer = new WebMarkupContainer("gradebook-text-points");
        gradebookSettingsPointsContainer.setOutputMarkupId(true);
        gradebookSettingsPointsContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsPointsContainer.setMarkupId("gradebook-text-points");
        gradebookSettingsPointsContainer.setVisible(hasGradebookInSite && hasGradebookItem);
        form.add(gradebookSettingsPointsContainer);

        /**
         * GB Points input
         */
        final TextField<?> gradebookSettingsPoints = new TextField<Double>("gradebook-input-text-points", new PropertyModel<Double>(articulateTCContentPackageSettings, "points"));
        gradebookSettingsPoints.setOutputMarkupId(true);
        gradebookSettingsPoints.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsPoints.setMarkupId("gradebook-input-text-points");
        gradebookSettingsPointsContainer.add(gradebookSettingsPoints);

        /**
         * GB checkbox input
         */
        AjaxCheckBox gradebookCheckboxSync = new AjaxCheckBox("gradebook-input-checkbox-sync", new PropertyModel<Boolean>(articulateTCContentPackageSettings, "graded")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isChecked = this.getConvertedInput();
                // delete verification message
                gradebookSettingsVerifyMessageContainer.setVisible(hasGradebookItem && !isChecked);
                target.addComponent(gradebookSettingsVerifyMessageContainer);
                // title row
                gradebookSettingsTitleContainer.setVisible(isChecked);
                target.addComponent(gradebookSettingsTitleContainer);
                // points row
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

    /**
     * Generates the gradebook item title, not allowing duplicates
     * 
     * @param articulateTCContentPackageSettings
     * @param context
     * @return
     */
    private String getItemTitle(ArticulateTCContentPackageSettings articulateTCContentPackageSettings, String context) {
        String fixedTitle = articulateTCContentPackageSettings.getGradebookItemTitle();
        int count = 1;

        while (gradebookService.isAssignmentDefined(context, fixedTitle)) {
            fixedTitle = articulateTCContentPackageSettings.getGradebookItemTitle() + " (" + count++ + ")";
        }
  
        return fixedTitle;
    }

    /**
     * Get the current site ID
     * 
     * @return
     */
    protected String getContext() {
        return developerHelperService.getCurrentLocationId();
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
