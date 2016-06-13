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
