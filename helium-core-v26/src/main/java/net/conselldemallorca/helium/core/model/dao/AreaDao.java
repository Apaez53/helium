/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Area;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes del tipus Area
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AreaDao extends HibernateGenericDao<Area, Long> {

	public AreaDao() {
		super(Area.class);
	}

	public List<Area> findAmbEntorn(Long entornId) {
		return findByCriteria(Restrictions.eq("entorn.id", entornId));
	}
	public List<Area> findPagedAndOrderedAmbEntorn(
			Long entornId,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				new String[] {sort},
				asc,
				Restrictions.eq("entorn.id", entornId));
	}
	public int getCountAmbEntorn(
			Long entornId) {
		return getCountByCriteria(Restrictions.eq("entorn.id", entornId));
	}
	public List<Area> findAreaAmbEntorn(
			Long entornId) {
		return findByCriteria(Restrictions.eq("entorn.id", entornId));
	}	
	public Area findAmbEntornICodi(
			Long entornId,
			String codi) {
		List<Area> arees = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (arees.size() > 0)
			return arees.get(0);
		return null;
	}
	public List<Area> findLikeNom(Long entornId, String text) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.ilike("nom", "%" + text + "%"));
	}
	public List<Area> findAmbTipus(Long tipusAreaId) {
		return findByCriteria(
				Restrictions.eq("tipus.id", tipusAreaId));
	}
	public List<Area> findAmbPare(Long entornId, String pareCodi) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("pare.codi", pareCodi));
	}

}
