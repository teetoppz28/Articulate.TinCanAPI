/*
 * #%L
 * SCORM Model Impl
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
package org.sakaiproject.articulate.tincan.impl.dao;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCContentPackageDaoImpl extends HibernateDaoSupport implements ArticulateTCContentPackageDao {

    public int countContentPackages(String context, String name) {
        int count = 0;
        List<ArticulateTCContentPackage> articulateTContentPackages = find(context);

        for (ArticulateTCContentPackage articulateTContentPackage : articulateTContentPackages) {
            Pattern p = Pattern.compile(name + "\\s*\\(?\\d*\\)?");
            Matcher m = p.matcher(articulateTContentPackage.getTitle());

            if (m.matches()) {
                count++;
            }
        }

        return count;
    }

    @SuppressWarnings("unchecked")
    public List<ArticulateTCContentPackage> find(String context) {
        String statement = new StringBuilder("from ").append(ArticulateTCContentPackage.class.getName()).append(" where context = ? and deleted = ? ").toString();

        return getHibernateTemplate().find(statement, new Object[] { context, false });
    }

    @Override
    public ArticulateTCContentPackage get(long id) {
        return (ArticulateTCContentPackage) getHibernateTemplate().get(ArticulateTCContentPackage.class, id);
    }

    @Override
    public ArticulateTCContentPackage load(long id) {
        return get(id);
    }

    @Override
    public void remove(ArticulateTCContentPackage articulateTCContentPackage) {
        articulateTCContentPackage.setDeleted(true);
        save(articulateTCContentPackage);
    }

    @Override
    public void save(ArticulateTCContentPackage articulateTCContentPackage) {
        Session session = null;

        try {
            // Since we are in a thread that doesn't have a hibernate session we need to manage it here
            session = getSessionFactory().openSession();
            TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));

            try {
                session.beginTransaction();
                articulateTCContentPackage.setModifiedOn(new Date());
                getHibernateTemplate().saveOrUpdate(articulateTCContentPackage);
            } catch (Throwable e) {
                logger.error("Error saving activity state data: ", e);
                session.getTransaction().rollback();
            } finally {
                if (!session.getTransaction().wasRolledBack()) {
                    session.flush();
                    session.getTransaction().commit();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }

            TransactionSynchronizationManager.unbindResource(getSessionFactory());
        }
    }

}
