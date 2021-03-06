/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus CampAgrupacio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CampAgrupacioDao extends HibernateGenericDao<CampAgrupacio, Long> {

	public CampAgrupacioDao() {
		super(CampAgrupacio.class);
	}

	@SuppressWarnings("unchecked")
	public List<CampAgrupacio> findAmbDefinicioProcesOrdenats(Long definicioProcesId) {
		return (List<CampAgrupacio>)getSession().
				createQuery(
						"from " +
						"    CampAgrupacio ca " +
						"where " +
						"    ca.definicioProces.id=? " +
						"order by " +
						"    ordre").
				setLong(0, definicioProcesId).
				list();
	}

	public CampAgrupacio findAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return (CampAgrupacio)getSession().
				createQuery(
						"from " +
						"    CampAgrupacio ca " +
						"where " +
						"    ca.definicioProces.id=? " +
						"and ca.codi=?").
				setLong(0, definicioProcesId).
				setString(1, codi).uniqueResult();
	}

	public int getNextOrder(Long definicioProcesId) {
		Object result = getSession().createQuery(
				"select " +
				"	 max(ca.ordre) " +
				"from " +
				"    CampAgrupacio ca " +
				"where " +
				"    ca.definicioProces.id=?").
				setLong(0, definicioProcesId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	public CampAgrupacio getAmbOrdre(Long definicioProcesId, int ordre) {
		return (CampAgrupacio)getSession().createQuery(
				"from " +
				"    CampAgrupacio ca " +
				"where " +
				"    ca.definicioProces.id=? " +
				"and ca.ordre=?").
				setLong(0, definicioProcesId).
				setInteger(1, ordre).
				uniqueResult();
	}

}
