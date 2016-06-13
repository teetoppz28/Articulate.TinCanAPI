package org.sakaiproject.articulate.tincan.impl.dao;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCContentPackageDaoImpl extends HibernateDaoSupport implements ArticulateTCContentPackageDao {

    private final Logger log = LoggerFactory.getLogger(ArticulateTCContentPackageDaoImpl.class);

    @Override
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
    @Override
    public List<ArticulateTCContentPackage> find(String context) {
        String statement = new StringBuilder(" FROM ").append(ArticulateTCContentPackage.class.getName()).append(" WHERE context = ? AND deleted = ? ").toString();

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
                log.error("Error saving articulate content package data: ", e);
                session.getTransaction().rollback();
            } finally {
                if (!session.getTransaction().wasRolledBack()) {
                    session.flush();
                    session.getTransaction().commit();
                }

                session.clear();
            }
        } finally {
            if (session != null) {
                session.close();
            }

            TransactionSynchronizationManager.unbindResource(getSessionFactory());
        }
    }

}
