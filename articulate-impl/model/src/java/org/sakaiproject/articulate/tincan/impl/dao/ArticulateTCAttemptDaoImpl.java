package org.sakaiproject.articulate.tincan.impl.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttempt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCAttemptDaoImpl extends HibernateDaoSupport implements ArticulateTCAttemptDao {

    private final Logger log = LoggerFactory.getLogger(ArticulateTCAttemptDaoImpl.class);

    @Override
    public int count(final long contentPackageId, final String learnerId) {
        HibernateCallback<Object> hcb = new HibernateCallback<Object>() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(ArticulateTCAttempt.class)
                    .add(Restrictions.eq("contentPackageId", contentPackageId))
                    .add(Restrictions.eq("learnerId", learnerId))
                    .setProjection(Projections.count("id"));

                return criteria.uniqueResult();
            }
        };

        Object result = getHibernateTemplate().execute(hcb);

        int r = 0;

        if (result != null) {
            if (result instanceof Number) {
                r = ((Number) result).intValue();
            }
        }

        return r;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticulateTCAttempt> find(long contentPackageId) {
        return (List<ArticulateTCAttempt>) getHibernateTemplate().find(" FROM " + ArticulateTCAttempt.class.getName() + " WHERE contentPackageId = ? ", new Object[] {contentPackageId});
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticulateTCAttempt> find(long contentPackageId, String learnerId) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(" FROM ").append(ArticulateTCAttempt.class.getName()).append(" WHERE contentPackageId = ? AND learnerId = ? ORDER BY attemptNumber DESC ");

        return getHibernateTemplate().find(buffer.toString(), new Object[] {contentPackageId, learnerId});
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticulateTCAttempt> find(String courseId, String learnerId) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(" FROM ").append(ArticulateTCAttempt.class.getName()).append(" WHERE courseId = ? AND learnerId = ? ORDER BY attemptNumber DESC ");

        return getHibernateTemplate().find(buffer.toString(), new Object[] {courseId, learnerId});
    }

    @Override
    public ArticulateTCAttempt find(String courseId, String learnerId, long attemptNumber) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(" FROM ").append(ArticulateTCAttempt.class.getName()).append(" WHERE courseId = ? AND learnerId = ? AND attemptNumber = ? ");
        
        @SuppressWarnings("unchecked")
        List<ArticulateTCAttempt> r = getHibernateTemplate().find(buffer.toString(), new Object[] {courseId, learnerId, attemptNumber});
        if (r.size() == 0) {
            return null;
        }

        return (ArticulateTCAttempt) r.get(r.size() - 1);
    }

    @Override
    public ArticulateTCAttempt load(long id) {
        return (ArticulateTCAttempt) getHibernateTemplate().load(ArticulateTCAttempt.class, id);
    }

    @Override
    public ArticulateTCAttempt lookup(final long contentPackageId, final String learnerId, final long attemptNumber) {
        HibernateCallback<Object> hcb = new HibernateCallback<Object>() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                StringBuilder buffer = new StringBuilder();
                buffer.append(" FROM ").append(ArticulateTCAttempt.class.getName()).append(" WHERE contentPackageId = ? AND learnerId = ? AND attemptNumber = ? ");

                Query query = session.createQuery(buffer.toString());
                query.setLong(0, contentPackageId);
                query.setString(1, learnerId);
                query.setLong(2, attemptNumber);

                return query.uniqueResult();
            }
        };

        ArticulateTCAttempt attempt = (ArticulateTCAttempt) getHibernateTemplate().execute(hcb);

        return attempt;
    }

    @Override
    public void save(ArticulateTCAttempt articulateTCAttempt) {
        Session session = null;

        try {
            // Since we are in a thread that doesn't have a hibernate session we need to manage it here
            session = getSessionFactory().openSession();
            TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));

            try {
                session.beginTransaction();
                articulateTCAttempt.setLastModifiedDate(new Date());
                getHibernateTemplate().saveOrUpdate(articulateTCAttempt);
            } catch (Throwable e) {
                log.error("Error saving attempt data", e);
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

    @Override
    public ArticulateTCAttempt lookupNewest(long contentPackageId, String learnerId) {
        // First figure out the highest attempt number...
        DetachedCriteria sub = DetachedCriteria.forClass(ArticulateTCAttempt.class)
            .add(Restrictions.eq("contentPackageId", contentPackageId))
            .add(Restrictions.eq("learnerId", learnerId))
            .setProjection(Projections.max("attemptNumber"));
        // Then use it as restriction
        DetachedCriteria criteria = DetachedCriteria.forClass(ArticulateTCAttempt.class)
            .add(Restrictions.eq("contentPackageId", contentPackageId))
            .add(Restrictions.eq("learnerId", learnerId))
            .add(Subqueries.propertyEq("attemptNumber", sub));

        return uniqueResult(criteria);
    }

    private ArticulateTCAttempt uniqueResult(final DetachedCriteria criteria) {
        return (ArticulateTCAttempt) getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria s = criteria.getExecutableCriteria(session);

                return s.uniqueResult();
            }
        });
    }

}
