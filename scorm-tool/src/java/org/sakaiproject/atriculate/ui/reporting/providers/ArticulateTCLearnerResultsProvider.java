package org.sakaiproject.atriculate.ui.reporting.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCLearnerResultsProvider extends SortableDataProvider<ArticulateTCAttemptResult> {

    private static final long serialVersionUID = 1L;

    private List<ArticulateTCAttemptResult> articulateTCAttemptResults;

    public ArticulateTCLearnerResultsProvider(List<ArticulateTCAttemptResult> articulateTCAttemptResults) {
        this.articulateTCAttemptResults = articulateTCAttemptResults;
        setSort("attemptNumber", true);
    }

    public Iterator<? extends ArticulateTCAttemptResult> iterator(int first, int count) {
        List<ArticulateTCAttemptResult> data = new ArrayList<>(articulateTCAttemptResults);

        Collections.sort(data, new Comparator<ArticulateTCAttemptResult>() {
            public int compare(ArticulateTCAttemptResult o1, ArticulateTCAttemptResult o2) {
                int dir = getSort().isAscending() ? 1 : -1;

                if (StringUtils.equalsIgnoreCase("attemptNumber", getSort().getProperty())) {
                    Long o1an = o1.getAttemptNumber() != null ? o1.getAttemptNumber() : 0L;
                    Long o2an = o2.getAttemptNumber() != null ? o2.getAttemptNumber() : 0L;
                    return dir * (o1an.compareTo(o2an));
                } else if (StringUtils.equalsIgnoreCase("scaledScore", getSort().getProperty())) {
                    Double o1gb = o1.getScaledScore() != null ? o1.getScaledScore() : 0.0;
                    Double o2gb = o2.getScaledScore() != null ? o2.getScaledScore() : 0.0;
                    return dir * (o1gb.compareTo(o2gb));
                } else if (StringUtils.equalsIgnoreCase("dateCompleted", getSort().getProperty())) {
                    return dir * (o1.getDateCompleted().compareTo(o2.getDateCompleted()));
                }

                return 0;
            }
        });

        return data.subList(first, Math.min(first + count, data.size())).iterator();
    }

    public int size() {
       return articulateTCAttemptResults != null ? articulateTCAttemptResults.size() : 0;
    }

    public IModel<ArticulateTCAttemptResult> model(ArticulateTCAttemptResult articulateTCAttemptResult) {
       return Model.of(articulateTCAttemptResult);
    }

}
