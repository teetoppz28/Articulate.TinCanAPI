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
import org.sakaiproject.articulate.tincan.model.ArticulateTCConstants;


public class ArticulateTCPlayerPage extends BaseToolPage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    

    public ArticulateTCPlayerPage() {
        this(new PageParameters());
    }

    public ArticulateTCPlayerPage(final PageParameters pageParams) {
        super();

        InlineFrame iframe = new InlineFrame("launch-frame", this){

            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getURL() {
                String launchUrl = pageParams.getString("url");
                // TODO testing this query statement
                //launchUrl += "?endpoint=%09https%3A%2F%2Fcloud.scorm.com%2Ftc%2FOZ13EN933G%2F&auth=Basic+aklDQjItalM2bzlsN0VQQ0NiNDpoSlNaV2JMT1lqUS1TV21Qc1Q4&actor=%7B\"name\"%3A%20%5B\"First%20Last\"%5D%2C%20\"mbox\"%3A%20%5B\"mailto%3Afirstlast%40mycompany.com\"%5D%7D";
                launchUrl += "?endpoint=%09http%3A%2F%2Flocalhost%2Fdirect%2Ftincanapi-lrs%2Faction%2F";
                launchUrl += "&auth=Basic+aklDQjItalM2bzlsN0VQQ0NiNDpoSlNaV2JMT1lqUS1TV21Qc1Q4";
                launchUrl += "&actor=%7B\"name\"%3A%20%5B\"First%20Last\"%5D%2C%20\"mbox\"%3A%20%5B\"mailto%3Afirstlast%40mycompany.com\"%5D%7D";

                return launchUrl;
            }
        };

        add(iframe);
    }

    public void renderHead(IHeaderResponse response) {
        response.renderJavascriptReference(HTML_HEADSCRIPTS);
        response.renderOnLoadJavascript(HTML_BODY_ONLOAD_ADDTL);
        response.renderCSSReference(TOOLBASE_CSS);
        response.renderCSSReference(TOOL_CSS);
        response.renderCSSReference(HTML_TINCANAPI_CSS);
    }

}
