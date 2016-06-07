package org.sakaiproject.atriculate.ui.console.components;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.scorm.model.api.ContentPackage;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class TriesColumn extends AbstractColumn<ContentPackage> {

    private static final long serialVersionUID = 1L;

    private transient ArticulateTCAttemptDao articulateTCAttemptDao;
    private transient DeveloperHelperService developerHelperService;

    public TriesColumn(IModel<String> displayModel, String sortProperty) {
        super(displayModel, sortProperty);

        developerHelperService = (DeveloperHelperService) ComponentManager.get(DeveloperHelperService.class);
        articulateTCAttemptDao = (ArticulateTCAttemptDao) ComponentManager.get(ArticulateTCAttemptDao.class);
    }

    public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
        item.add(new Label(componentId, createLabelModel(model)));
    }

    protected String createLabelModel(IModel<ContentPackage> embeddedModel) {
        Object target = embeddedModel.getObject();

        if (target instanceof ArticulateTCContentPackage) {
            ArticulateTCContentPackage contentPackage = (ArticulateTCContentPackage) target;

            int attemptsCount = articulateTCAttemptDao.count(contentPackage.getContentPackageId(), developerHelperService.getCurrentUserId());
            int maxAttempts = contentPackage.getNumberOfTries();
            String tries = Integer.toString(attemptsCount) + " / ";

            if (maxAttempts == -1) {
                return tries + "unlimited";
            }

            return  tries + Integer.toString(maxAttempts);
        }

        return "";
    }

}
