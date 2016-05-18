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
package org.sakaiproject.atriculate.ui.console.pages;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.ui.console.pages.DisplayDesignatedPackage;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.wicket.markup.html.form.CancelButton;
import org.sakaiproject.wicket.model.DecoratedPropertyModel;
import org.sakaiproject.wicket.model.SimpleDateFormatPropertyModel;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCPackageConfigurationPage extends ArticulateTCConsoleBasePage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    @SpringBean(name="org.sakaiproject.entitybroker.DeveloperHelperService")
    private DeveloperHelperService developerHelperService;

    @SpringBean(name="org.sakaiproject.service.gradebook.GradebookService")
    private GradebookService gradebookService;

    @SpringBean
    private LearningManagementSystem lms;

    @SpringBean(name="org.sakaiproject.tool.api.ToolManager")
    private ToolManager toolManager;

    private boolean hasGradebookInSite = false;
    private boolean hasGradebookItem = false;
    private String unlimitedMessage;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ArticulateTCPackageConfigurationPage(final PageParameters params) {
        super(params);

        long contentPackageId = params.getLong("contentPackageId");
        final ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.load(contentPackageId);

        Form<Object> form = new Form<Object>("configurationForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                Assignment assignment = null;
                if (articulateTCContentPackage.getAssignmentId() != null) {
                    assignment = gradebookService.getAssignment(getContext(), articulateTCContentPackage.getAssignmentId());
                }

                boolean hasAssignmentDefined = assignment != null;
                boolean gradebookChecked = articulateTCContentPackage.isGraded();
                String fixedTitle = getItemTitle(articulateTCContentPackage, getContext());
                Double points = articulateTCContentPackage.getPoints();

                if (hasAssignmentDefined && gradebookChecked) {
                    Double previousPoints = assignment.getPoints();
                    assignment.setDueDate(articulateTCContentPackage.getDueOn());
                    assignment.setPoints(points);
                    gradebookService.updateAssignment(getContext(), assignment.getName(), assignment);
                    // update the scores for the new points, if changed
                    if (previousPoints != points) {
                        articulateTCEntityProviderService.updateScaledScores(getContext(), assignment.getId(), previousPoints);
                    }
                } else if (!hasAssignmentDefined && gradebookChecked) {
                    assignment = new Assignment();
                    assignment.setName(fixedTitle);
                    assignment.setDueDate(articulateTCContentPackage.getDueOn());
                    assignment.setPoints(points);
                    assignment.setCounted(true);
                    gradebookService.addAssignment(getContext(), assignment);
                    // sync the assignment IDs
                    assignment = gradebookService.getAssignment(getContext(), assignment.getName());
                    articulateTCContentPackage.setAssignmentId(assignment.getId());
                } else if (hasAssignmentDefined && !gradebookChecked) {
                    gradebookService.removeAssignment(assignment.getId());
                    // reset gradebook item title to package title
                    articulateTCContentPackage.setGradebookItemTitle(articulateTCContentPackage.getTitle());
                    // reset gradebook item points to default
                    articulateTCContentPackage.setPoints(CONFIGURATION_DEFAULT_POINTS);
                    // reset the assignment ID
                    articulateTCContentPackage.setAssignmentId(null);
                }

                articulateTCContentPackageDao.save(articulateTCContentPackage);

                setResponsePage(params.getBoolean("no-toolbar") ? DisplayDesignatedPackage.class : ArticulateTCPackageListPage.class);
            }
        };

        List<Integer> tryList = new LinkedList<Integer>();

        tryList.add(-1);
        for (int i = 1; i <= CONFIGURATION_DEFAULT_ATTEMPTS; i++) {
            tryList.add(i);
        }

        this.unlimitedMessage = getLocalizer().getString("unlimited", this);

        TextField<?> nameField = new TextField<ArticulateTCContentPackage>("packageName", new PropertyModel<ArticulateTCContentPackage>(articulateTCContentPackage, "title"));
        nameField.setRequired(true);
        form.add(nameField);

        DateTimeField releaseOnDTF = new DateTimeField("releaseOnDTF", new PropertyModel(articulateTCContentPackage, "releaseOn"));
        releaseOnDTF.setRequired(true);
        form.add(releaseOnDTF);
        form.add(new DateTimeField("dueOnDTF", new PropertyModel(articulateTCContentPackage, "dueOn")));
        form.add(new DateTimeField("acceptUntilDTF", new PropertyModel(articulateTCContentPackage, "acceptUntil")));
        form.add(new DropDownChoice<Object>("numberOfTries", new PropertyModel<Object>(articulateTCContentPackage, "numberOfTries"), tryList, new TryChoiceRenderer()));
        form.add(new Label("createdBy", new DisplayNamePropertyModel(articulateTCContentPackage, "createdBy")));
        form.add(new Label("createdOn", new SimpleDateFormatPropertyModel(articulateTCContentPackage, "createdOn")));
        form.add(new Label("modifiedBy", new DisplayNamePropertyModel(articulateTCContentPackage, "modifiedBy")));
        form.add(new Label("modifiedOn", new SimpleDateFormatPropertyModel(articulateTCContentPackage, "modifiedOn")));

        hasGradebookInSite = gradebookService.isGradebookDefined(getContext());

        // get the current gradebook item, if it exists
        Assignment assignment = null;
        if (hasGradebookInSite && articulateTCContentPackage.getAssignmentId() != null) {
            assignment = gradebookService.getAssignment(getContext(), articulateTCContentPackage.getAssignmentId());
            hasGradebookItem = assignment != null;
        }

        // set if this item is already in the gradebook
        articulateTCContentPackage.setGraded(hasGradebookItem);
        // set default gb title to content package
        articulateTCContentPackage.setGradebookItemTitle(hasGradebookItem ? assignment.getName() : articulateTCContentPackage.getTitle());
        // set default points
        articulateTCContentPackage.setPoints(hasGradebookItem ? assignment.getPoints() : CONFIGURATION_DEFAULT_POINTS);

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
        final TextField<?> gradebookSettingsTitle = new TextField<String>("gradebook-input-text-title", new PropertyModel<String>(articulateTCContentPackage, "gradebookItemTitle"));
        gradebookSettingsTitle.setOutputMarkupId(true);
        gradebookSettingsTitle.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsTitle.setMarkupId("gradebook-input-text-title");
        gradebookSettingsTitle.setVisible(!hasGradebookItem); // only editable on first load
        gradebookSettingsTitleContainer.add(gradebookSettingsTitle);

        /**
         * GB Title non-editable text
         */
        final Label gradebookSettingsTitleLabel = new Label("gradebook-input-text-title-label", new PropertyModel<String>(articulateTCContentPackage, "gradebookItemTitle"));
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
        final TextField<?> gradebookSettingsPoints = new TextField<Double>("gradebook-input-text-points", new PropertyModel<Double>(articulateTCContentPackage, "points"));
        gradebookSettingsPoints.setOutputMarkupId(true);
        gradebookSettingsPoints.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsPoints.setMarkupId("gradebook-input-text-points");
        gradebookSettingsPointsContainer.add(gradebookSettingsPoints);

        /**
         * GB Record Score container
         */
        final WebMarkupContainer gradebookSettingsRecordScoreContainer = new WebMarkupContainer("gradebook-checkbox-record-score");
        gradebookSettingsRecordScoreContainer.setOutputMarkupId(true);
        gradebookSettingsRecordScoreContainer.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsRecordScoreContainer.setMarkupId("gradebook-checkbox-record-score");
        gradebookSettingsRecordScoreContainer.setVisible(hasGradebookInSite && hasGradebookItem);
        form.add(gradebookSettingsRecordScoreContainer);

        /**
         * GB Record Score radio group input
         */
        final RadioGroup gradebookSettingsRecordScoreRadioGroup = new RadioGroup("gradebook-checkbox-record-score-radio-group", new PropertyModel<String>(articulateTCContentPackage, "recordType"));
        gradebookSettingsRecordScoreRadioGroup.setOutputMarkupId(true);
        gradebookSettingsRecordScoreRadioGroup.setOutputMarkupPlaceholderTag(true);
        gradebookSettingsRecordScoreRadioGroup.setMarkupId("gradebook-checkbox-record-score-radio-group");
        gradebookSettingsRecordScoreRadioGroup.add(new Radio("gradebook-input-record-best", new Model<String>(CONFIGURATION_RECORD_SCORE_TYPE_BEST)));
        gradebookSettingsRecordScoreRadioGroup.add(new Radio("gradebook-input-record-latest", new Model<String>(CONFIGURATION_RECORD_SCORE_TYPE_LATEST)));
        gradebookSettingsRecordScoreContainer.add(gradebookSettingsRecordScoreRadioGroup);

        /**
         * GB checkbox input
         */
        AjaxCheckBox gradebookCheckboxSync = new AjaxCheckBox("gradebook-input-checkbox-sync", new PropertyModel<Boolean>(articulateTCContentPackage, "graded")) {
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
                // record type row
                gradebookSettingsRecordScoreContainer.setVisible(isChecked);
                target.addComponent(gradebookSettingsRecordScoreContainer);
            }
        };
        gradebookCheckboxSync.setOutputMarkupId(true);
        gradebookCheckboxSync.setOutputMarkupPlaceholderTag(true);
        gradebookCheckboxSync.setMarkupId("gradebook-input-checkbox-sync");
        gradebookSettingsCheckboxContainer.add(gradebookCheckboxSync);

        form.add(new CancelButton("cancel", (params.getBoolean("no-toolbar")) ? DisplayDesignatedPackage.class : ArticulateTCPackageListPage.class));

        add(form);
    }

    /**
     * Generates the gradebook item title, not allowing duplicates
     * 
     * @param articulateTCContentPackage
     * @param context
     * @return
     */
    private String getItemTitle(ArticulateTCContentPackage articulateTCContentPackage, String context) {
        String fixedTitle = articulateTCContentPackage.getGradebookItemTitle();

        int count = 1;

        while (gradebookService.isAssignmentDefined(context, fixedTitle)) {
            fixedTitle = articulateTCContentPackage.getGradebookItemTitle() + " (" + count++ + ")";
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
