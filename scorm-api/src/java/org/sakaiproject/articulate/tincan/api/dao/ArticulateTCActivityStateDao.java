package org.sakaiproject.articulate.tincan.api.dao;

import java.util.List;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;

public interface ArticulateTCActivityStateDao {

    public void save(ArticulateTCActivityState articulateTCActivityState);

    public ArticulateTCActivityState get(String id);

    public ArticulateTCActivityState load(String id);

    public List<ArticulateTCActivityState> findByContext(String context, boolean deleted);

    void remove(ArticulateTCActivityState articulateTCActivityState);

}
