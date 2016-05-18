package org.sakaiproject.articulate.tincan.impl.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCAttemptResultDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCAttemptResultDaoImpl extends HibernateDaoSupport implements ArticulateTCAttemptResultDao {

    @Override
    public void save(ArticulateTCAttemptResult articulateTCAttemptResult) {
        Session session = null;

        try {
            // Since we are in a thread that doesn't have a hibernate session we need to manage it here
            session = getSessionFactory().openSession();
            TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));

            try {
                session.beginTransaction();
                articulateTCAttemptResult.setModified(new Date());
                getHibernateTemplate().saveOrUpdate(articulateTCAttemptResult);
            } catch (Throwable e) {
                logger.error("Error saving articulate attempt results data: ", e);
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

    @Override
    public ArticulateTCAttemptResult get(long id) {
        return (ArticulateTCAttemptResult) getHibernateTemplate().get(ArticulateTCAttemptResult.class, id);
    }

    @Override
    public ArticulateTCAttemptResult load(long id) {
        ArticulateTCAttemptResult articulateTCAttemptResult = null;

        try {
            articulateTCAttemptResult = (ArticulateTCAttemptResult) getHibernateTemplate().load(ArticulateTCAttemptResult.class, id);
        } catch (DataAccessException e) {
            articulateTCAttemptResult = get(id);
        }

        return articulateTCAttemptResult;
    }

    @Override
    public List<ArticulateTCAttemptResult> findByAttemptId(long attemptId) {
        return findByAttemptId(attemptId, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticulateTCAttemptResult> findByAttemptId(long attemptId, boolean onlyCompleted) {
        String where = " WHERE attemptId = ? ";

        if (onlyCompleted) {
            where += "AND dateCompleted IS NOT NULL ";
        }

        String statement = new StringBuilder(" FROM ").append(ArticulateTCAttemptResult.class.getName()).append(where).toString();

        List<ArticulateTCAttemptResult> list = getHibernateTemplate().find(statement, new Object[] {attemptId});

        // no row exists with the attempt ID
        if (list.isEmpty()) {
            return null;
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticulateTCAttemptResult> findByAttemptIdIncomplete(long attemptId) {
        String statement = new StringBuilder(" FROM ").append(ArticulateTCAttemptResult.class.getName()).append(" WHERE attemptId = ? AND dateCompleted IS NULL ").toString();

        List<ArticulateTCAttemptResult> list = getHibernateTemplate().find(statement, new Object[] {attemptId});

        // no row exists with the attempt ID
        if (list.isEmpty()) {
            return null;
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArticulateTCAttemptResult findByAttemptIdNumber(long attemptId, long attemptNumber) {
        String statement = new StringBuilder(" FROM ").append(ArticulateTCAttemptResult.class.getName()).append(" WHERE attemptId = ? AND attemptNumber = ? ").toString();

        List<ArticulateTCAttemptResult> list = getHibernateTemplate().find(statement, new Object[] {attemptId, attemptNumber});

        // no row exists with the package ID
        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public boolean isAttemptComplete(long attemptId) {
        ArticulateTCAttemptResult articulateTCAttemptResult = get(attemptId);

        if (articulateTCAttemptResult == null) {
            return false;
        }

        return articulateTCAttemptResult.getDateCompleted() != null;
    }

}
