package org.sakaiproject.articulate.tincan.impl.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCContentPackageSettingsDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCContentPackageSettings;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ArticulateTCContentPackageSettingsDaoImpl extends HibernateDaoSupport implements ArticulateTCContentPackageSettingsDao {

    @Override
    public void save(ArticulateTCContentPackageSettings articulateTCContentPackageSettings) {
        Session session = null;

        try {
            // Since we are in a thread that doesn't have a hibernate session we need to manage it here
            session = getSessionFactory().openSession();
            TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));

            try {
                session.beginTransaction();
                articulateTCContentPackageSettings.setModified(new Date());
                getHibernateTemplate().saveOrUpdate(articulateTCContentPackageSettings);
            } catch (Throwable e) {
                logger.error("Error saving articulate content package settings data: ", e);
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
    public ArticulateTCContentPackageSettings get(long id) {
        return (ArticulateTCContentPackageSettings) getHibernateTemplate().get(ArticulateTCContentPackageSettings.class, id);
    }

    @Override
    public ArticulateTCContentPackageSettings load(long id) {
        ArticulateTCContentPackageSettings articulateTCContentPackageSettings = null;

        try {
            articulateTCContentPackageSettings = (ArticulateTCContentPackageSettings) getHibernateTemplate().load(ArticulateTCContentPackageSettings.class, id);
        } catch (DataAccessException e) {
            articulateTCContentPackageSettings = get(id);
        }

        return articulateTCContentPackageSettings;
    }

    @Override
    public void remove(long id) {
        ArticulateTCContentPackageSettings articulateTCContentPackageSettings = get(id);

        if (articulateTCContentPackageSettings == null) {
            return;
        }

        getHibernateTemplate().delete(articulateTCContentPackageSettings);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArticulateTCContentPackageSettings findOneByPackageId(long packageId) {
        String statement = new StringBuilder(" FROM ").append(ArticulateTCContentPackageSettings.class.getName()).append(" WHERE packageId = ? ").toString();

        List<ArticulateTCContentPackageSettings> list = getHibernateTemplate().find(statement, new Object[] {packageId});

        // no row exists with the package ID
        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public ArticulateTCContentPackageSettings findOneByPackageId(String packageId) {
        return findOneByPackageId(Long.parseLong(packageId));
    }

    @Override
    public boolean isArticulateContentPackage(long packageId) {
        ArticulateTCContentPackageSettings articulateTCContentPackageSettings = findOneByPackageId(packageId);

        if (articulateTCContentPackageSettings == null) {
            return false;
        }

        return true;
    }

}
