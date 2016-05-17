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

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.atriculate.ui.upload.pages.ArticulateTCUploadPage;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.ui.Icon;
import org.sakaiproject.scorm.ui.console.components.BreadcrumbPanel;
import org.sakaiproject.scorm.ui.console.components.SakaiFeedbackPanel;
import org.sakaiproject.scorm.ui.console.pages.MaydayWebMarkupContainer;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.wicket.markup.html.SakaiPortletWebPage;
import org.sakaiproject.wicket.markup.html.link.NavIntraLink;

public class ArticulateTCConsoleBasePage extends SakaiPortletWebPage implements ArticulateTCConstants, IHeaderContributor {

    private static final long serialVersionUID = 1L;

    protected final static String RES_CONSOLE_PREFIX = "../../../../scorm/ui/console/pages/";
    protected final static String RES_REPORTING_PREFIX = "../../../../scorm/ui/reporting/pages/";
    protected final static ResourceReference CONSOLE_CSS = new CompressedResourceReference(ArticulateTCConsoleBasePage.class, RES_CONSOLE_PREFIX + "res/scorm_console.css");
    private static ResourceReference LIST_ICON = new ResourceReference(ArticulateTCConsoleBasePage.class, RES_CONSOLE_PREFIX + "res/table.png");
    private static ResourceReference UPLOAD_ICON = new ResourceReference(ArticulateTCConsoleBasePage.class, RES_CONSOLE_PREFIX + "res/table_add.png");
    private final static String SAK_PROP_ENABLE_MENU_BUTTON_ICONS = "scorm.menuButton.icons";
    protected final static String SAK_PROP_SCORM_ENABLE_EMAIL = "scorm.enable.email";

    // The feedback panel component displays dynamic messages to the user
    protected FeedbackPanel feedback;
    private BreadcrumbPanel breadcrumbs;

    @SpringBean
    protected LearningManagementSystem lms;

    @SpringBean(name="articulateTCEntityProviderService")
    protected ArticulateTCEntityProviderService articulateTCEntityProviderService;

    @SpringBean(name="articulateTCContentPackageDao")
    protected ArticulateTCContentPackageDao articulateTCContentPackageDao;

    @SpringBean( name = "org.sakaiproject.component.api.ServerConfigurationService" )
    protected ServerConfigurationService serverConfigurationService;

    @SpringBean
    private ToolManager toolManager;

    public ArticulateTCConsoleBasePage() {
        this(null);
    }

    public ArticulateTCConsoleBasePage(PageParameters params) {
        final String context = lms.currentContext();
        final boolean canUpload = lms.canUpload(context);
        final boolean canValidate = lms.canValidate(context);

        WebMarkupContainer wmc = new MaydayWebMarkupContainer("toolbar-administration");

        if (isSinglePackageTool()) {
            wmc.setVisible(false);
        }

        NavIntraLink listLink = new NavIntraLink("listLink", new ResourceModel("link.list"), ArticulateTCPackageListPage.class);
        NavIntraLink uploadLink = new NavIntraLink("uploadLink", new ResourceModel("link.upload"), ArticulateTCUploadPage.class);
        NavIntraLink articulateTCUploadLink = new NavIntraLink("articulate-tc-upload-link", new ResourceModel("link.upload.articulate.tc"), ArticulateTCUploadPage.class);

        WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        WebMarkupContainer uploadContainer = new WebMarkupContainer("uploadContainer");WebMarkupContainer articulateTCUploadContainer = new WebMarkupContainer( "articulate-tc-upload-container" );
        articulateTCUploadContainer.setVisible(canUpload);
        articulateTCUploadContainer.add(articulateTCUploadLink);
        listContainer.add(listLink);
        uploadContainer.add(uploadLink);

        SimpleAttributeModifier className = new SimpleAttributeModifier("class", "current");
        if (listLink.linksTo(getPage())) {
            listContainer.add(className);
            listLink.add(className);
        } else if (uploadLink.linksTo(getPage())) {
            uploadContainer.add(className);
            uploadLink.add(className);
        } else if (articulateTCUploadLink.linksTo(getPage())) {
            articulateTCUploadContainer.add(className);
            articulateTCUploadLink.add(className);
        }

        listLink.setVisible(canUpload || canValidate);
        uploadLink.setVisible(canUpload);

        Icon listIcon = new Icon("listIcon", LIST_ICON);
        Icon uploadIcon = new Icon("uploadIcon", UPLOAD_ICON);
        Icon articulateTCUploadIcon = new Icon("articulate-tc-upload-icon", UPLOAD_ICON);

        boolean enableMenuBarIcons = serverConfigurationService.getBoolean(SAK_PROP_ENABLE_MENU_BUTTON_ICONS, true);
        if (enableMenuBarIcons) {
            listIcon.setVisible(canUpload || canValidate);
            uploadIcon.setVisible(canUpload);
            articulateTCUploadIcon.setVisible(canUpload);
        } else {
            listIcon.setVisibilityAllowed(false);
            uploadIcon.setVisibilityAllowed(false);
            articulateTCUploadIcon.setVisibilityAllowed(false);
        }
        
        listContainer.add(listIcon);
        uploadContainer.add(uploadIcon);
        articulateTCUploadContainer.add(articulateTCUploadIcon);

        wmc.add(listContainer);
        wmc.add(uploadContainer);
        wmc.add(articulateTCUploadContainer);

        add(wmc);

        add(newPageTitleLabel(params));
        add(feedback = new SakaiFeedbackPanel("feedback"));
        add(breadcrumbs = new BreadcrumbPanel("breadcrumbs"));

        Icon pageIcon = new Icon("pageIcon", getPageIconReference());
        pageIcon.setVisible(getPageIconReference() != null);
        add(pageIcon);
    }

    public void addBreadcrumb(IModel model, Class<?> pageClass, PageParameters params, boolean isEnabled) {
        breadcrumbs.addBreadcrumb(model, pageClass, params, isEnabled);
    }

    protected Label newPageTitleLabel(PageParameters params) {
        return new Label("page.title", new ResourceModel("page.title"));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        feedback.setVisible(hasFeedbackMessage());
        breadcrumbs.setVisible(breadcrumbs.getNumberOfCrumbs() > 0);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CONSOLE_CSS);
        response.renderCSSReference(HTML_ARTICULATE_TC_CSS);
    }

    protected ResourceReference getPageIconReference() {
        return null;
    }

    protected boolean isSinglePackageTool() {
        return toolManager != null && toolManager.getCurrentTool() != null && "sakai.scorm.singlepackage.tool".equals(toolManager.getCurrentTool().getId());
    }

}
