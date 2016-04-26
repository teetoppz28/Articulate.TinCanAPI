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
package org.sakaiproject.scorm.ui.player.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCLaunchService;
import org.sakaiproject.scorm.ui.console.pages.ConsoleBasePage;

public class ArticulateTCPlayerPage extends ConsoleBasePage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    @SpringBean(name="articulateTCLaunchService")
    private ArticulateTCLaunchService articulateTCLaunchService;

    public ArticulateTCPlayerPage() {
        this(new PageParameters());
    }

    public ArticulateTCPlayerPage(final PageParameters pageParams) {
        super();

        final String packageId = pageParams.getString("contentPackageId");

        // record the attempt
        articulateTCLaunchService.addAttempt(packageId);

        /**
         * iFrame that holds the Articulate TinCanAPI content player
         */
        InlineFrame iframe = new InlineFrame("launch-frame", this){
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getURL() {
                return pageParams.getString("url") + articulateTCLaunchService.calculateLaunchParams(packageId);
            }
        };

        add(iframe);
    }

    public void renderHead(IHeaderResponse response) {
        response.renderJavascriptReference(HTML_HEADSCRIPTS);
        response.renderOnLoadJavascript(HTML_BODY_ONLOAD_ADDTL);
        response.renderCSSReference(TOOLBASE_CSS);
        response.renderCSSReference(TOOL_CSS);
        response.renderCSSReference(HTML_ARTICULATE_TC_CSS);
    }

}
