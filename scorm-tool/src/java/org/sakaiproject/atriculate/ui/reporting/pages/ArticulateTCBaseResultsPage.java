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
import org.apache.wicket.markup.html.IHeaderResponse;
import org.sakaiproject.scorm.ui.console.pages.ConsoleBasePage;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCBaseResultsPage extends ConsoleBasePage {

    private static final long serialVersionUID = 1L;

    public ArticulateTCBaseResultsPage(PageParameters pageParams) {
        super(pageParams);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CONSOLE_CSS);
        response.renderCSSReference(HTML_ARTICULATE_TC_CSS);
        response.renderCSSReference(HTML_BOOTSTRAP_CSS);
    }

}
