package org.sakaiproject.articulate.tincan.impl.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCActivityStateDao;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ArticulateTCActivityStateDaoImpl extends HibernateDaoSupport implements ArticulateTCActivityStateDao {

    @Override
    public void save(ArticulateTCActivityState articulateTCActivityState) {
        Session session = null;

        try {
            // Since we are in a thread that doesn't have a hibernate session we need to manage it here
            session = getSessionFactory().openSession();
            TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));

            try {
                session.beginTransaction();
                articulateTCActivityState.setModified(new Date());
                getHibernateTemplate().saveOrUpdate(articulateTCActivityState);
            } catch (Throwable e) {
                logger.error("problem saving state data:", e);
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
    public ArticulateTCActivityState get(long id) {
        return (ArticulateTCActivityState) getHibernateTemplate().get(ArticulateTCActivityState.class, id);
    }

    @Override
    public ArticulateTCActivityState load(long id) {
        ArticulateTCActivityState articulateTCActivityState = null;

        try {
            articulateTCActivityState = (ArticulateTCActivityState) getHibernateTemplate().load(ArticulateTCActivityState.class, id);
        } catch (DataAccessException e) {
            articulateTCActivityState = get(id);
        }

        return articulateTCActivityState;
    }

    @Override
    public void remove(ArticulateTCActivityState articulateTCActivityState) {
        articulateTCActivityState.setDeleted(true);
        save(articulateTCActivityState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ArticulateTCActivityState> findByContext(String context, boolean deleted) {
        String statement = new StringBuilder(" FROM ").append(ArticulateTCActivityState.class.getName()).append(" WHERE registration = ? AND deleted = ? ").toString();

        return getHibernateTemplate().find(statement, new Object[] {context, deleted});
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArticulateTCActivityState findOneByUniqueKey(String userId, String siteId, String packageId) {
        String statement = new StringBuilder(" FROM ").append(ArticulateTCActivityState.class.getName()).append(" WHERE userId = ? AND registration = ? AND packageId = ? ").toString();

        List<ArticulateTCActivityState> list = getHibernateTemplate().find(statement, new Object[] {userId, siteId, packageId});

        // no row exists with the unique key
        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public ArticulateTCActivityState findOne(ArticulateTCRequestPayload articulateTCRequestPayload) {
        if (!articulateTCRequestPayload.isValid()) {
            return null;
        }

        return findOneByUniqueKey(articulateTCRequestPayload.getUserId(), articulateTCRequestPayload.getSiteId(), articulateTCRequestPayload.getPackageId());
    }
}
