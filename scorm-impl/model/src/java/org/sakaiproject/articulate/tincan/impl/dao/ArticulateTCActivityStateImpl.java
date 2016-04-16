package org.sakaiproject.articulate.tincan.impl.dao;

import java.util.Date;
import java.util.List;

import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCActivityStateDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ArticulateTCActivityStateImpl extends HibernateDaoSupport implements ArticulateTCActivityStateDao {

    @Override
    public void save(ArticulateTCActivityState articulateTCActivityState) {
        Date now = new Date();
        ArticulateTCActivityState existingState = get(articulateTCActivityState.getId());

        if (existingState == null) {
            articulateTCActivityState.setCreated(now);
        }

        articulateTCActivityState.setModified(now);
        getHibernateTemplate().saveOrUpdate(articulateTCActivityState);
    }

    @Override
    public ArticulateTCActivityState get(String id) {
        return (ArticulateTCActivityState) getHibernateTemplate().get(ArticulateTCActivityState.class, id);
    }

    @Override
    public ArticulateTCActivityState load(String id) {
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

}
