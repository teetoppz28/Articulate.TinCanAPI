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
package org.sakaiproject.atriculate.ui.upload.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.atriculate.ui.console.pages.ArticulateTCConsoleBasePage;

public class ArticulateTCConfirmPage extends ArticulateTCConsoleBasePage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    public ArticulateTCConfirmPage(PageParameters params) {
        final String filename = params.getString("filename");
        final int status = params.getInt("status");

        info(getNotification(status));

        add(new Label("title", "File"));
        add(new Label("filename", filename));
    }

    private String getNotification(int status) {
        String resultKey = getKey(status);

        return getLocalizer().getString(resultKey, this);
    }
    
    private String getKey(int status) {
        switch (status) {
        case VALIDATION_SUCCESS:
            return "validate.success";
        case VALIDATION_WRONGMIMETYPE:
            return "validate.wrong.mime.type";
        case VALIDATION_NOFILE:
            return "validate.no.file";
        case VALIDATION_NOMANIFEST:
            return "validate.no.manifest";
        case VALIDATION_NOTWELLFORMED:
            return "validate.not.well.formed";
        case VALIDATION_NOTVALIDROOT:
            return "validate.not.valid.root";
        case VALIDATION_NOTVALIDSCHEMA:
            return "validate.not.valid.schema";
        case VALIDATION_NOTVALIDPROFILE:
            return "validate.not.valid.profile";
        case VALIDATION_MISSINGREQUIREDFILES:
            return "validate.missing.files";
        case VALIDATION_CONVERTFAILED:
            return "validate.convert.failed";
        };

        return "validate.failed";
    }

}
