package org.sakaiproject.articulate.tincan.impl.dao;

import org.sakaiproject.articulate.tincan.api.dao.ArticulateTCActivityStateDao;
import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ArticulateTCActivityStateImpl extends HibernateDaoSupport implements ArticulateTCActivityStateDao {

    @Override
    public void save(ArticulateTCActivityState articulateTCActivityState) {
        getHibernateTemplate().saveOrUpdate(articulateTCActivityState);
    }

}
