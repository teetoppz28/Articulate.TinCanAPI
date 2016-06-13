package org.sakaiproject.atriculate.ui.upload.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.scorm.ui.console.pages.ConsoleBasePage;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCConfirmPage extends ConsoleBasePage implements ArticulateTCConstants {

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
