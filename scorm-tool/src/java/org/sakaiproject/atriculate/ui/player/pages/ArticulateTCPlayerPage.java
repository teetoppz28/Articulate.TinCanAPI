package org.sakaiproject.atriculate.ui.player.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.api.ArticulateTCLaunchService;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.scorm.ui.player.pages.BaseToolPage;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCPlayerPage extends BaseToolPage implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    @SpringBean(name="articulateTCLaunchService")
    private ArticulateTCLaunchService articulateTCLaunchService;

    public ArticulateTCPlayerPage() {
        this(new PageParameters());
    }

    public ArticulateTCPlayerPage(final PageParameters pageParams) {
        super();

        final String contentPackageId = pageParams.getString("contentPackageId");

        // record the attempt
        articulateTCLaunchService.addAttempt(contentPackageId);

        /**
         * iFrame that holds the Articulate TinCanAPI content player
         */
        InlineFrame iframe = new InlineFrame("launch-frame", this){
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getURL() {
                return pageParams.getString("url") + articulateTCLaunchService.calculateLaunchParams(contentPackageId);
            }
        };

        add(iframe);

        /**
         * Link to return to the package list page
         */
        add(new Link<Void>("link-return") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(PackageListPage.class, pageParams);
            }

            @Override
            public boolean isVisible() {
                return true;
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(HTML_ARTICULATE_TC_CSS);
        response.renderCSSReference(HTML_BOOTSTRAP_CSS);
        response.renderJavascriptReference(HTML_HEADSCRIPTS);
        response.renderOnLoadJavascript(HTML_BODY_ONLOAD_ADDTL);
    }

}
