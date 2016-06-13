package org.sakaiproject.atriculate.ui.upload.pages;

import java.util.Arrays;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxPostprocessingCallDecorator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.sakaiproject.articulate.tincan.api.ArticulateTCImporterService;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.sakaiproject.scorm.ui.console.pages.ConsoleBasePage;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.wicket.markup.html.form.CancelButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCUploadPage extends ConsoleBasePage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(ArticulateTCUploadPage.class);

    @SpringBean(name="articulateTCImporterService")
    private ArticulateTCImporterService articulateTCImporterService;

    @SpringBean(name="org.sakaiproject.scorm.service.api.ScormResourceService")
    protected ScormResourceService resourceService;

    @SpringBean(name = "org.sakaiproject.component.api.ServerConfigurationService")
    private ServerConfigurationService serverConfigurationService;

    public ArticulateTCUploadPage(PageParameters params) {
        add(new FileUploadForm("uploadForm"));
    }

    public class FileUploadForm extends Form<ArticulateTCUploadPage> {
        private static final long serialVersionUID = 1L;
        private FileUploadField fileUploadField;
        private boolean fileHidden = false;
        private int priority = NotificationService.NOTI_NONE;
        private FileUpload fileInput;

        public FileUpload getFileInput() {
            return fileInput;
        }

        public void setFileInput(FileUpload fileUpload) {
            this.fileInput = fileUpload;
        }

        public FileUploadForm(String id) {
            super(id);

            this.setModel(new CompoundPropertyModel<ArticulateTCUploadPage>(this));

            // We need to establish the largest file allowed to be uploaded
            setMaxSize(Bytes.megabytes(resourceService.getMaximumUploadFileSize()));

            // create a feedback panel, setMaxMessages not in this version
            final Component feedbackPanel = new FeedbackPanel("feedback").setOutputMarkupPlaceholderTag(true);

            setMultiPart(true);

            // Add JavaScript to enable the submit button only when there is a file selected (this cannot be done via Wicket/Java code)
            fileUploadField = new FileUploadField("fileInput");
            fileUploadField.add( new AttributeAppender("onchange", new Model<String>("document.getElementsByName( \"btnSubmit\" )[0].disabled = this.value === '';"), ";"));
            add(fileUploadField);

            @SuppressWarnings({"unchecked", "rawtypes"})
            DropDownChoice emailNotificationDropDown = new DropDownChoice(
                "priority",
                Arrays.asList(new Integer[] {NotificationService.NOTI_NONE, NotificationService.NOTI_OPTIONAL, NotificationService.NOTI_REQUIRED}),
                new IChoiceRenderer() {
                    private static final long serialVersionUID = 1L;

                    public Object getDisplayValue(Object object) {
                        switch(((Integer) object)) {
                            case NotificationService.NOTI_NONE: {
                                return getLocalizer().getString("NotificationService.NOTI_NONE", ArticulateTCUploadPage.this);
                            }
                            case NotificationService.NOTI_OPTIONAL: {
                                return getLocalizer().getString("NotificationService.NOTI_OPTIONAL", ArticulateTCUploadPage.this);
                            }
                            case NotificationService.NOTI_REQUIRED: {
                                return getLocalizer().getString("NotificationService.NOTI_REQUIRED", ArticulateTCUploadPage.this);
                            }
                        }

                        return "";
                    }

                    public String getIdValue(Object object, int index) {
                        if(object == null) {
                            return "";
                        }

                        return object.toString();
                    }
                }
            );

            boolean enableEmail = serverConfigurationService.getBoolean(SAK_PROP_SCORM_ENABLE_EMAIL, true);
            Label priorityLabel = new Label("lblPriority", new ResourceModel("upload.priority.label"));

            if(!enableEmail) {
                emailNotificationDropDown.setEnabled(false);
                emailNotificationDropDown.setVisibilityAllowed(false);
                priorityLabel.setEnabled(false);
                priorityLabel.setVisibilityAllowed(false);
            }

            add(priorityLabel);
            add(emailNotificationDropDown);

            final CancelButton btnCancel = new CancelButton("btnCancel", PackageListPage.class);
            IndicatingAjaxButton btnSubmit = new IndicatingAjaxButton("btnSubmit", this) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    FeedbackMessages feedbackMessages = form.getSession().getFeedbackMessages();

                    if (!feedbackMessages.isEmpty()) {
                        log.info("Errors uploading file: {}", feedbackMessages.toString());
                    }

                    target.addComponent(feedbackPanel);
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return new AjaxPostprocessingCallDecorator(super.getAjaxCallDecorator()) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public CharSequence postDecorateScript(CharSequence script) {
                            // Disable the submit and cancel buttons on click
                            return script + "this.disabled = true; document.getElementsByName( \"btnCancel\" )[0].disabled = true;";
                        }
                    };
                }

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if(fileUploadField != null) {
                        final FileUpload upload = fileUploadField.getFileUpload();

                        if(upload != null) {
                            try {
                                int status = articulateTCImporterService.validateAndProcess(upload.getInputStream(), upload.getClientFileName(), upload.getContentType());
                                if(status == VALIDATION_SUCCESS) {
                                    setResponsePage(PackageListPage.class);
                                } else {
                                    PageParameters params = new PageParameters();
                                    params.add("filename", upload.getClientFileName());
                                    params.put("status", status);
                                    setResponsePage(ArticulateTCConfirmPage.class, params);
                                }
                            } catch(Exception e) {
                                ArticulateTCUploadPage.this.warn(getLocalizer().getString("upload.failed", ArticulateTCUploadPage.this, new Model<Exception>(e)));
                                log.error("Failed to upload file", e);
                            }
                        }
                    }
                }
            };

            add(btnCancel);
            add(btnSubmit);
            add(feedbackPanel);
        }

        public boolean isFileHidden() {
            return fileHidden;
        }

        public void setFileHidden(boolean fileHidden) {
            this.fileHidden = fileHidden;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }

}
