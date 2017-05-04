/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Termini;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un termini que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiRepository extends JpaRepository<Termini, Long> {

	Termini findByDefinicioProcesAndCodi(
			DefinicioProces definicioProces,
			String codi);
	
	@Query(	"select t from " +
			"    Termini t " +
			"where " +
			"    t.definicioProces.id = :definicioProcesId")
	List<Termini> findByDefinicioProcesId(@Param("definicioProcesId") Long definicioProcesId);

	@Query(	"select t from " +
			"    Termini t " +
			"where " +
			"	 t.id not in (:exclude) " + 
			"    and (t.expedientTipus.id = :expedientTipusId " + 
			"	 		or t.expedientTipus.id = :expedientTipusPareId) " + 
			"order by t.codi asc ")
	List<Termini> findByExpedientTipusAmbHerencia(
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("expedientTipusPareId") Long expedientTipusPareId, 
			@Param("exclude") Set<Long> exclude);

	Termini findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);

	@Query(	"from Termini t " +
			"where " +
			"	t.id not in (:exclude) " +
			"   and (t.expedientTipus.id = :expedientTipusId or t.expedientTipus.id = :expedientTipusPareId or  t.expedientTipus.id is null) " +
			"   and (t.definicioProces.id = :definicioProcesId or t.definicioProces.id is null) " +
			"	and (:esNullFiltre = true or lower(t.codi) like lower('%'||:filtre||'%') or lower(t.nom) like lower('%'||:filtre||'%')) ")
	Page<Termini> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("expedientTipusPareId") Long expedientTipusPareId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("exclude") Set<Long> exclude, 
			Pageable pageable);	
	
	@Query("select t from " +
			"    Termini t " +
			"where " +
			"    t.expedientTipus.id=:expedientTipusId " +
			"order by " +
			"    codi")
	List<Termini> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);

	@Query( "select ts " +
			"from Termini t " +
			"	join t.expedientTipus et with et.id = :expedientTipusId, " +
			"	Termini ts " +
			"where " +
			"	ts.codi = t.codi " +
			" 	and ts.expedientTipus.id = et.expedientTipusPare.id ")
	List<Termini> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);
}
