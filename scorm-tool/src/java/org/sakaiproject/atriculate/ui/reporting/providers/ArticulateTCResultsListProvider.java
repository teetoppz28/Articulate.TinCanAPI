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
import org.sakaiproject.articulate.tincan.model.ArticulateTCMemberAttemptResult;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCResultsListProvider extends SortableDataProvider<ArticulateTCMemberAttemptResult> {

    private static final long serialVersionUID = 1L;

    private List<ArticulateTCMemberAttemptResult> articulateTCMemberAttemptResults;

    public ArticulateTCResultsListProvider(List<ArticulateTCMemberAttemptResult> articulateTCMemberAttemptResults) {
        this.articulateTCMemberAttemptResults = articulateTCMemberAttemptResults;
        setSort("fullName", true);
    }

    public Iterator<? extends ArticulateTCMemberAttemptResult> iterator(int first, int count) {
        List<ArticulateTCMemberAttemptResult> data = new ArrayList<>(articulateTCMemberAttemptResults);

        Collections.sort(data, new Comparator<ArticulateTCMemberAttemptResult>() {
            public int compare(ArticulateTCMemberAttemptResult o1, ArticulateTCMemberAttemptResult o2) {
                int dir = getSort().isAscending() ? 1 : -1;

                if (StringUtils.equalsIgnoreCase("fullName", getSort().getProperty())) {
                    int sort = dir * (o1.getLastName().compareTo(o2.getLastName()));

                    if (sort == 0) {
                        // last names match, sort by first
                        sort = dir * (o1.getFirstName().compareTo(o2.getFirstName()));
                    }

                    return sort;
                } else if (StringUtils.equalsIgnoreCase("gradebookScore", getSort().getProperty())) {
                    String o1gb = StringUtils.isNotBlank(o1.getGradebookScore()) ? o1.getGradebookScore() : "0";
                    String o2gb = StringUtils.isNotBlank(o2.getGradebookScore()) ? o2.getGradebookScore() : "0";

                    return dir * (o1gb.compareTo(o2gb));
                } else if (StringUtils.equalsIgnoreCase("eid", getSort().getProperty())) {
                    return dir * (o1.getEid().compareTo(o2.getEid()));
                } else if (StringUtils.equalsIgnoreCase("attemptNumber", getSort().getProperty())) {
                    Long o1an = 0L;
                    Long o2an = 0L;

                    if (StringUtils.isNotBlank(o1.getAttemptNumber())) {
                        o1an = Long.parseLong(o1.getAttemptNumber());
                    }

                    if (StringUtils.isNotBlank(o2.getAttemptNumber())) {
                        o2an = Long.parseLong(o2.getAttemptNumber());
                    }

                    return dir * (o1an.compareTo(o2an));
                }

                return 0;
            }
        });

        return data.subList(first, Math.min(first + count, data.size())).iterator();
    }

    public int size() {
       return articulateTCMemberAttemptResults != null ? articulateTCMemberAttemptResults.size() : 0;
    }

    public IModel<ArticulateTCMemberAttemptResult> model(ArticulateTCMemberAttemptResult articulateTCMemberAttemptResult) {
       return Model.of(articulateTCMemberAttemptResult);
    }

}
