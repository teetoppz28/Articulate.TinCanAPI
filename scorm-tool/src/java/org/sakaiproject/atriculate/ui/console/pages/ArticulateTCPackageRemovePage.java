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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxPostprocessingCallDecorator;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.api.ArticulateTCDeleteService;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.wicket.markup.html.form.CancelButton;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCPackageRemovePage extends ArticulateTCConsoleBasePage {

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(ArticulateTCPackageRemovePage.class);

    @SpringBean(name="articulateTCDeleteService")
    private ArticulateTCDeleteService articulateTCDeleteService;

    public ArticulateTCPackageRemovePage(final PageParameters params) {
        super(params);

        add(new FileRemoveForm("removeForm", params));
    }

    public class FileRemoveForm extends Form<Object> {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public FileRemoveForm(String id, final PageParameters params) {
            super(id);

            final long contentPackageId = params.getLong("contentPackageId");
            final ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.load(contentPackageId);

            List<ArticulateTCContentPackage> list = new LinkedList<ArticulateTCContentPackage>();
            list.add(articulateTCContentPackage);

            List<IColumn<Object>> columns = new LinkedList<IColumn<Object>>();
            columns.add(new PropertyColumn<Object>(new Model<String>("Content Package"), "title", "title"));

            DataTable<ArticulateTCContentPackage> removeTable = new DataTable<ArticulateTCContentPackage>("removeTable", columns.toArray(new IColumn[columns.size()]), new ListDataProvider<ArticulateTCContentPackage>(list), 3);

            final Label alertLabel = new Label("alert", new ResourceModel("verify.remove"));
            final CancelButton btnCancel = new CancelButton("btnCancel", PackageListPage.class);
            IndicatingAjaxButton btnSubmit = new IndicatingAjaxButton( "btnSubmit", this ) {
                private static final long serialVersionUID = 1L;

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return new AjaxPostprocessingCallDecorator(super.getAjaxCallDecorator()) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public CharSequence postDecorateScript(CharSequence script) {
                            // Disable the submit and cancel buttons on click
                            return script + "this.disabled=true;document.getElementsByName(\"btnCancel\")[0].disabled=true;";
                        }
                    };
                }

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    // remove gradebook assignment
                    boolean gbSuccess = articulateTCDeleteService.deleteGradebookItem(contentPackageId);

                    // remove the resource files
                    boolean resourceSucess = articulateTCDeleteService.deleteResourceFiles(contentPackageId);

                    // remove content package
                    boolean cpSuccess = articulateTCDeleteService.deleteContentPackage(contentPackageId);

                    setResponsePage(ArticulateTCPackageListPage.class);

                    if (!gbSuccess || !resourceSucess || !cpSuccess) {
                        LOG.warn("Failed to delete all underlying resources for content package ID: " + contentPackageId);
                        alertLabel.setDefaultModel(new ResourceModel("exception.remove"));
                        alertLabel.setOutputMarkupId(true);
                        target.addComponent(alertLabel);

                        setResponsePage(ArticulateTCPackageRemovePage.class, params);
                    }
                }
            };

            add(alertLabel);
            add(removeTable);
            add(btnCancel);
            add(btnSubmit);
        }
    }

}
