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
package org.sakaiproject.atriculate.ui.reporting.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.articulate.tincan.model.ArticulateTCMemberAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.sakaiproject.atriculate.ui.reporting.providers.ArticulateTCResultsListProvider;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;

public class ArticulateTCResultsListPage extends ArticulateTCBaseResultsPage {

    private static final long serialVersionUID = 1L;

    private static final ResourceReference PAGE_ICON = new ResourceReference(ArticulateTCLearnerResultsPage.class, RES_PREFIX + "res/report.png");

    public ArticulateTCResultsListPage(PageParameters pageParams) {
        super(pageParams);

        List<ArticulateTCMemberAttemptResult> articulateTCMemberAttemptResults = new ArrayList<ArticulateTCMemberAttemptResult>();
        long contentPackageId = pageParams.getLong("contentPackageId");
        ArticulateTCContentPackage articulateTCContentPackage = articulateTCContentPackageDao.get(contentPackageId);
        Long assignmentId = articulateTCContentPackage.getAssignmentId();

        String siteId = articulateTCContentPackage.getContext();
        Site site = null;

        try {
            site = siteService.getSite(siteId);
        } catch (IdUnusedException e) {
            // site doesn't exist
            e.printStackTrace();
        }

        // get students
        String maintainRole = site.getMaintainRole();
        Set<Member> members = site.getMembers();
        Iterator<Member> iterator = members.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();
            if (StringUtils.equalsIgnoreCase(member.getRole().getId(), maintainRole)) {
                // remove maintainers
                iterator.remove();
            }
        }

        // get attempts for each student
        for (Member member : members) {
            String firstName = "[First name unknown]";
            String lastName = "[Last name unknown]";
            String fullName = "[Name unknown]";
            User user = null;

            try {
                user = userDirectoryService.getUser(member.getUserId());
                firstName = user.getFirstName();
                lastName = user.getLastName();
                fullName = user.getDisplayName();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArticulateTCAttempt articulateTCAttempt = articulateTCAttemptDao.lookupNewest(contentPackageId, member.getUserId());

            List<ArticulateTCAttemptResult> articulateTCAttemptResults = null;

            if (articulateTCAttempt != null) {
                articulateTCAttemptResults = articulateTCAttemptResultDao.findByAttemptId(articulateTCAttempt.getId());
            }

            String gradebookScore = "-";
            if (assignmentId != null) {
                gradebookScore = gradebookService.getAssignmentScoreString(siteId, assignmentId, member.getUserId());
            }

            ArticulateTCMemberAttemptResult articulateTCMemberAttemptResult = new ArticulateTCMemberAttemptResult();
            articulateTCMemberAttemptResult.setUserId(member.getUserId());
            articulateTCMemberAttemptResult.setEid(member.getUserEid());
            articulateTCMemberAttemptResult.setFirstName(firstName);
            articulateTCMemberAttemptResult.setLastName(lastName);
            articulateTCMemberAttemptResult.setFullName(fullName);
            articulateTCMemberAttemptResult.setArticulateTCAttempt(articulateTCAttempt);
            articulateTCMemberAttemptResult.setArticulateTCAttemptResults(articulateTCAttemptResults);
            articulateTCMemberAttemptResult.setGradebookScore(gradebookScore);

            articulateTCMemberAttemptResults.add(articulateTCMemberAttemptResult);
        }

        List<IColumn<ArticulateTCMemberAttemptResult>> columns = new ArrayList<IColumn<ArticulateTCMemberAttemptResult>>();
        columns.add(new ArticulateTCStudentReportLinkPanel<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.name", this, null), "fullName", "fullName", pageParams));
        columns.add(new PropertyColumn<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.id", this, null), "eid", "eid"));
        columns.add(new PropertyColumn<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.attempts", this, null), "attemptNumber", "articulateTCAttempt.attemptNumber"));
        columns.add(new PropertyColumn<ArticulateTCMemberAttemptResult>(new StringResourceModel("column.header.gradebook.score", this, null), "gradebookScore", "gradebookScore"));

        BasicDataTable dataTable = new BasicDataTable("results-table", columns, new ArticulateTCResultsListProvider(articulateTCMemberAttemptResults));
        add(dataTable);

    }

    @Override
    protected ResourceReference getPageIconReference() {
        return PAGE_ICON;
    }

}
