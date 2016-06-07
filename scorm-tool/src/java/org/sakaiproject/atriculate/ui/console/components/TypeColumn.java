package org.sakaiproject.atriculate.ui.console.components;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.scorm.model.api.ContentPackage;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class TypeColumn extends AbstractColumn<ContentPackage> implements ArticulateTCConstants {

    private static final long serialVersionUID = 1L;

    public TypeColumn(IModel<String> displayModel, String sortProperty) {
        super(displayModel, sortProperty);
    }

    public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
        item.add(new Label(componentId, createLabelModel(model)));
    }

    protected String createLabelModel(IModel<ContentPackage> embeddedModel) {
        Object contentPackage = embeddedModel.getObject();

        if (contentPackage instanceof ArticulateTCContentPackage) {
            return CONFIGURATION_DEFAULT_APP_CONTENT_TYPE;
        } else if (contentPackage instanceof ContentPackage) {
            return "SCORM 2004 v3";
        } else {
            return "unknown";
        }
    }

}
