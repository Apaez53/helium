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

import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un domini que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiRepository extends JpaRepository<Domini, Long> {

	Domini findByExpedientTipusAndCodi(
			ExpedientTipus expedientTipus,
			String codi);

	Domini findByEntornAndCodi(
			Entorn entorn,
			String codi);
	
	/** Cerca un domini global per entorn i codi. */
	@Query(	"from " +
			"    Domini d " +
			"where " +
			"    d.entorn = :entorn " +
			"and d.codi = :codi " +
			"and d.expedientTipus is null ")
	Domini findByEntornAndCodiAndExpedientTipusNull(
			@Param("entorn") Entorn entorn,
			@Param("codi") String codi);
	
	/** Consulta la llista de tots els dominis d'un entorn siguin o no globals. */
	List<Domini> findByEntorn(
			Entorn entorn);

	@Query("select e from " +
			"    Domini e " +
			"where " +
			"    e.expedientTipus.id = :expedientTipusId " +
			"order by " +
			"    nom")
	List<Domini> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);

	@Query(	"from Domini d " +
			"where " +
			"   d.entorn.id = :entornId " +
			"	and d.id not in (:exclude) " +
			"	and ((d.expedientTipus.id = :expedientTipusId) " + 
			"			or (d.expedientTipus.id = :expedientTipusPareId) " + 
			"			or (d.expedientTipus is null and (:esNullExpedientTipusId = true or :incloureGlobals = true))) " +
			"	and (:esNullFiltre = true or lower(d.codi) like lower('%'||:filtre||'%') or lower(d.nom) like lower('%'||:filtre||'%')) ")
	Page<Domini> findByFiltrePaginat(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("expedientTipusPareId") Long expedientTipusPareId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("exclude") Set<Long> exclude, 
			Pageable pageable);

	/** Troba les enumeracions per a un tipus d'expedient i també les globals de l'entorn i les ordena per nom.*/
	@Query(	"from Domini d " +
			"where " +
			"   d.entorn.id = (select entorn.id from ExpedientTipus ex where ex.id = :expedientTipusId) " +
			"	and ((d.expedientTipus.id = :expedientTipusId) or (d.expedientTipus is null )) " +
			"order by " +
			"	nom")
	List<Domini> findAmbExpedientTipusIGlobals(
			@Param("expedientTipusId") Long expedientTipusId);
	
	/** Troba per entorn i codi global amb expedient tipus null. */
	@Query(	"from " +
			"    Domini d " +
			"where " +
			"    d.entorn.id = :entornId " +
			"and d.expedientTipus is null ")
	List<Domini> findGlobals(
			@Param("entornId") Long entornId);

	@Query( "select ds " +
			"from Domini d " +
			"	join d.expedientTipus et with et.id = :expedientTipusId, " +
			"	Domini ds " +
			"where " +
			"	ds.codi = d.codi " +
			" 	and ds.expedientTipus.id = et.expedientTipusPare.id ")
	List<Domini> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);
}
