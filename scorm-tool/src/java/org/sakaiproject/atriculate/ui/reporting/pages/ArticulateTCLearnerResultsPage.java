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

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;

public class ArticulateTCLearnerResultsPage extends ArticulateTCBaseResultsPage {

    private static final long serialVersionUID = 1L;

    private static final ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCLearnerResultsPage.class, RES_PREFIX + "res/report_user.png");

    public ArticulateTCLearnerResultsPage(PageParameters pageParams) {
        super(pageParams);

        String userId = pageParams.getString("userId");
        System.out.println(pageParams.toString());
        add(new Label("boom", userId));
    }

    @Override
    protected ResourceReference getPageIconReference() {
        return PAGE_ICON;
    }

}
