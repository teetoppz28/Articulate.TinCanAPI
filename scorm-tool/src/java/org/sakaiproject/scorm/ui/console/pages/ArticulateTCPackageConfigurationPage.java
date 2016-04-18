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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.adl.validator.contentpackage.LaunchData;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
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

    @SpringBean
    LearningManagementSystem lms;

    @SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
    ScormContentService contentService;

    @SpringBean(name = "org.sakaiproject.service.gradebook.GradebookExternalAssessmentService")
    GradebookExternalAssessmentService gradebookExternalAssessmentService;

    private String unlimitedMessage;
    private static ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCPackageConfigurationPage.class, "res/table_edit.png");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ArticulateTCPackageConfigurationPage(final PageParameters params) {
        super(params);
        long contentPackageId = params.getLong("contentPackageId");

        final ContentPackage contentPackage = contentService.getContentPackage(contentPackageId);
        final GradebookSetup gradebookSetup = getAssessmentSetup(contentPackage);

        Form<Object> form = new Form<Object>("configurationForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                if (gradebookSetup.isGradebookDefined()) {
                    List<AssessmentSetup> assessments = gradebookSetup.getAssessments();

                    for (AssessmentSetup assessmentSetup : assessments) {
                        boolean on = assessmentSetup.issynchronizeWithGradebook();
                        String assessmentExternalId = getAssessmentExternalId(gradebookSetup, assessmentSetup);
                        String context = getContext();
                        boolean has = gradebookExternalAssessmentService.isExternalAssignmentDefined(context, assessmentExternalId);
                        String fixedTitle = getItemTitle(assessmentSetup, context);

                        if (has && on) {
                            gradebookExternalAssessmentService.updateExternalAssessment(context, assessmentExternalId, null, fixedTitle, assessmentSetup.numberOfPoints, gradebookSetup.getContentPackage().getDueOn(), false);
                        } else if (!has && on) {
                            gradebookExternalAssessmentService.addExternalAssessment(context, assessmentExternalId, null, fixedTitle, assessmentSetup.numberOfPoints, gradebookSetup.getContentPackage().getDueOn(), "Articulate TinCanAPI player", false);
                        } else if (has && !on) {
                            gradebookExternalAssessmentService.removeExternalAssessment(context, assessmentExternalId);
                        }
                    }
                }

                contentService.updateContentPackage(contentPackage);

                if(params.getBoolean("no-toolbar")) {
                    setResponsePage(DisplayDesignatedPackage.class);
                } else {
                    setResponsePage(PackageListPage.class);
                }
            }

            protected String getItemTitle(AssessmentSetup assessmentSetup, String context) {
                String fixedTitle = assessmentSetup.getItemTitle();
                int count = 1;
                while (gradebookExternalAssessmentService.isAssignmentDefined(context, fixedTitle)) {
                    fixedTitle = assessmentSetup.getItemTitle() + " (" + count++ + ")";
                }
                return fixedTitle;
            }
        };

        List<Integer> tryList = new LinkedList<Integer>();

        tryList.add(-1);
        for (int i = 1; i <= CONFIGURATION_DEFAULT_ATTEMPTS; i++) {
            tryList.add(i);
        }

        this.unlimitedMessage = getLocalizer().getString("unlimited", this);

        TextField<?> nameField = new TextField<Object>("packageName", new PropertyModel<Object>(contentPackage, "title"));
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

        ListView scos;
        form.add(scos = new ListView<AssessmentSetup>("scos", gradebookSetup.getAssessments()) {

            private static final long serialVersionUID = 965550162166385688L;

            @Override
            protected void populateItem(final ListItem item) {
                Label label = new Label("itemTitle", new PropertyModel(item.getModelObject(), "itemTitle"));
                item.add(label);
                final WebMarkupContainer verifySyncWithGradebook = new WebMarkupContainer("verifySyncWithGradebook");
                verifySyncWithGradebook.setOutputMarkupId(true);
                verifySyncWithGradebook.setOutputMarkupPlaceholderTag(true);
                verifySyncWithGradebook.setVisible(false);
                item.add(verifySyncWithGradebook);

                AjaxCheckBox synchronizeSCOWithGradebook = new AjaxCheckBox("synchronizeWithGradebook", new PropertyModel(item.getModelObject(), "synchronizeWithGradebook")) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        AssessmentSetup as = (AssessmentSetup) item.getModelObject();
                        String assessmentExternalId = getAssessmentExternalId(gradebookSetup, as);
                        boolean hasGradebookSync = gradebookExternalAssessmentService.isExternalAssignmentDefined(getContext(), assessmentExternalId);
                        boolean isChecked = this.getModelObject();
                        verifySyncWithGradebook.setVisible(hasGradebookSync && !isChecked);
                        target.addComponent(verifySyncWithGradebook);
                    }
                };

                item.add(synchronizeSCOWithGradebook);
            }
        });

        //scos.setVisible(gradebookSetup.isGradebookDefined() && !gradebookSetup.getAssessments().isEmpty());

        form.add(new CancelButton("cancel", (params.getBoolean("no-toolbar")) ? DisplayDesignatedPackage.class : PackageListPage.class));

        add(form);
    }

    private GradebookSetup getAssessmentSetup(ContentPackage contentPackage) {
        final GradebookSetup gradebookSetup = new GradebookSetup();
        String context = getContext();
        boolean isGradebookDefined = gradebookExternalAssessmentService.isGradebookDefined(context);
        gradebookSetup.setGradebookDefined(isGradebookDefined);
        gradebookSetup.setContentPackage(contentPackage);

        if (isGradebookDefined) {
            List<AssessmentSetup> assessments = gradebookSetup.getAssessments();
            for (AssessmentSetup as : assessments) {
                String assessmentExternalId = getAssessmentExternalId(gradebookSetup, as);
                boolean has = gradebookExternalAssessmentService.isExternalAssignmentDefined(getContext(), assessmentExternalId);
                as.setsynchronizeWithGradebook(has);
            }
        }

        return gradebookSetup;
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

    private static String getAssessmentExternalId(final GradebookSetup gradebook, AssessmentSetup assessment) {
        return gradebook.getContentPackageId() + ":" + assessment.getLaunchData().getItemIdentifier();
    }

    /*
     * Inner classes
     */

    public static class AssessmentSetup implements Serializable {
        private static final long serialVersionUID = 1L;

        private LaunchData launchData;
        private Double numberOfPoints = CONFIGURATION_DEFAULT_POINTS;
        boolean synchronizeWithGradebook;

        public AssessmentSetup() {
            super();
        }

        public AssessmentSetup(LaunchData launchData) {
            super();
            this.launchData = launchData;
        }

        public String getItemIdentifier() {
            return launchData.getItemIdentifier();
        }
        public String getItemTitle() {
            return launchData.getItemTitle();
        }

        public LaunchData getLaunchData() {
            return launchData;
        }

        public Double getNumberOfPoints() {
            return numberOfPoints;
        }

        public boolean issynchronizeWithGradebook() {
            return synchronizeWithGradebook;
        }

        public void setLaunchData(LaunchData launchData) {
            this.launchData = launchData;
        }

        public void setNumberOfPoints(Double numberOffPoints) {
            this.numberOfPoints = numberOffPoints;
        }

        public void setsynchronizeWithGradebook(boolean synchronizeWithGradebook) {
            this.synchronizeWithGradebook = synchronizeWithGradebook;
        }
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

    public static class GradebookSetup implements Serializable {
        private static final long serialVersionUID = 1L;

        List<AssessmentSetup> assessments = new ArrayList<ArticulateTCPackageConfigurationPage.AssessmentSetup>();
        boolean isGradebookDefined;
        ContentPackage contentPackage;

        public ContentPackage getContentPackage() {
            return contentPackage;
        }

        public void setContentPackage(ContentPackage contentPackage) {
            this.contentPackage = contentPackage;
        }

        public List<AssessmentSetup> getAssessments() {
            return assessments;
        }

        public String getContentPackageId() {
            return Long.toString(contentPackage.getContentPackageId());
        }

        public boolean isGradebookDefined() {
            return isGradebookDefined;
        }

        protected AssessmentSetup buildAssessmentSetup(LaunchData launchData) {
            AssessmentSetup assessment = new AssessmentSetup(launchData);
            return assessment;
        }

        public void setGradebookDefined(boolean isGradebookDefined) {
            this.isGradebookDefined = isGradebookDefined;
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
