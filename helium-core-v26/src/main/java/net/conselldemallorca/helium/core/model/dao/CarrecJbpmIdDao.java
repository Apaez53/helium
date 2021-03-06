/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.CarrecJbpmId;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus CarrecJbpmId
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CarrecJbpmIdDao extends HibernateGenericDao<CarrecJbpmId, Long> {

	public CarrecJbpmIdDao() {
		super(CarrecJbpmId.class);
	}

	@SuppressWarnings("unchecked")
	public List<CarrecJbpmId> findSenseAssignar() {
		List<CarrecJbpmId> resposta = new ArrayList<CarrecJbpmId>();
		Query query = getSession().createQuery(
				"select " +
				"    distinct m.role," +
				"    m.group.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    (m.role, m.group.name) not in (" +
				"        select " +
				"            c.codi," +
				"            c.grup " +
				"        from " +
				"            CarrecJbpmId c) ");
		List<Object[]> files = query.list();
		for (Object[] fila: files) {
			CarrecJbpmId carrec = new CarrecJbpmId();
			carrec.setCodi((String)fila[0]);
			carrec.setGrup((String)fila[1]);
			resposta.add(carrec);
		}
		return resposta;
	}

	public CarrecJbpmId findAmbCodiGrup(String codi, String grup) {
		List<CarrecJbpmId> carrecs = findByCriteria(
				Restrictions.eq("codi", codi),
				Restrictions.eq("grup", grup));
		if (carrecs.size() > 0) {
			return carrecs.get(0);
		} else {
			for (CarrecJbpmId carrec: findSenseAssignar()) {
				if (carrec.getCodi().equals(codi) && carrec.getGrup().equals(grup))
					return carrec;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String findPersonaAmbGrupICarrec(
			final String grup,
			final String carrec) {
		Query query = getSession().createQuery(
				"select " +
				"    m.user.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    m.group.name = :codiGroup " +
				"and m.role = :codiCarrec");
		query.setString("codiGroup", grup);
		query.setString("codiCarrec", carrec);
		List<String> files = query.list();
		if (files.size() > 0)
			return files.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> findPersonesAmbGrupICarrec(
			final String grup,
			final String carrec) {
		Query query = getSession().createQuery(
				"select " +
				"    m.user.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    m.group.name = :codiGroup " +
				"and m.role = :codiCarrec");
		query.setString("codiGroup", grup);
		query.setString("codiCarrec", carrec);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findCarrecsCodiAmbPersonaGrup(final String codiPersona, final String codiArea) {
		Query query = getSession().createQuery(
				"select " +
				"    m.role " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    m.user.name = :codiPersona " +
				"and m.group.name = :codiArea");
		query.setString("codiPersona", codiPersona);
		query.setString("codiArea", codiArea);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findPersonesAmbGrup(final String group) {
		Query query = getSession().createQuery(
				"select distinct " +
				"    m.user.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    m.group.name = :group");
		query.setString("group", group);
		return query.list();
	}

	/* Per suprimir */
	@SuppressWarnings("unchecked")
	public List<String> findPersonesAmbCarrecCodi(final String codi) {
		Query query = getSession().createQuery(
				"select " +
				"    m.user.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    m.role = :role");
		query.setString("role", codi);
		return query.list();
	}
	
	public CarrecJbpmId findAmbCodi(String codi) {
		List<CarrecJbpmId> carrecs = findByCriteria(
				Restrictions.eq("codi", codi));
		if (carrecs.size() > 0)
			return carrecs.get(0);
		return null;
	}
}
