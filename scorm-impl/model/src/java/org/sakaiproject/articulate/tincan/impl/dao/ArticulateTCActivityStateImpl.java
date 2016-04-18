package org.sakaiproject.articulate.tincan.impl.dao;

import java.util.Date;
import java.util.List;

import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCActivityStateDao;
import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ArticulateTCActivityStateImpl extends HibernateDaoSupport implements ArticulateTCActivityStateDao {

    @Override
    public void save(ArticulateTCActivityState articulateTCActivityState) {
        articulateTCActivityState.setModified(new Date());
        getHibernateTemplate().saveOrUpdate(articulateTCActivityState);
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
        String statement = new StringBuilder("from ").append(ArticulateTCActivityState.class.getName()).append(" where registration = ? and deleted = ? ").toString();

        return getHibernateTemplate().find(statement, new Object[] {context, deleted});
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArticulateTCActivityState findOneByUniqueKey(String userId, String siteId, String packageId) {
        String statement = new StringBuilder("from ").append(ArticulateTCActivityState.class.getName()).append(" where userId = ? AND registration = ? and packageId = ? ").toString();

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
