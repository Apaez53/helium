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

import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un estat que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EstatRepository extends JpaRepository<Estat, Long> {

	public List<Estat> findByExpedientTipusOrderByOrdreAsc(
			ExpedientTipus expedientTipus);

	/** Consulta per expedient tipus i el codi. Té en compte l'herència. */
	@Query(	"from Estat e " +
			"where " +
			"  	e.id = :id " +
			"  	and (e.expedientTipus.id = :expedientTipusId " +
			"			or e.expedientTipus.id = ( " + 
			"				select et.expedientTipusPare.id " + 
			"				from ExpedientTipus et " + 
			"				where et.id = :expedientTipusId)) ")
	public Estat findByExpedientTipusAndId(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("id") Long id);
	
	/** Consulta per expedient tipus id i el codi. No té en compte l'herència. */
	public Estat findByExpedientTipusIdAndCodi(
			Long expedientTipusId,
			String codi);
	
	@Query("select max(e.ordre) "
			+ "from Estat e "
			+ "where e.expedientTipus.id=:expedientTipusId")
	public Integer getSeguentOrdre(@Param("expedientTipusId") Long expedientTipusId);

	@Query(	"from Estat e " +
			"where " +
			"	e.id not in (:exclude) " +
			"  	and (e.expedientTipus.id = :expedientTipusId " +
			"			or e.expedientTipus.id = :expedientTipusPareId) " +
			"	and (:esNullFiltre = true or lower(e.codi) like lower('%'||:filtre||'%') or lower(e.nom) like lower('%'||:filtre||'%')) " +
			"order by e.ordre asc")
	Page<Estat> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("expedientTipusPareId") Long expedientTipusPareId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("exclude") Set<Long> exclude, 
			Pageable pageable);
	
	/** Troba tots els estats donat un tipus d'expedient, incloent els del tipus pare i excloent els ids passats per paràmetre.*/
	@Query(	"from Estat e " +
			"where " +
			"	e.id not in (:exclude) " +
			"  	and (e.expedientTipus.id = :expedientTipusId " +
			"			or e.expedientTipus.id = :expedientTipusPareId) " +
			"order by e.ordre asc")
	public List<Estat> findAllAmbHerencia(
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("expedientTipusPareId") Long expedientTipusPareId, 
			@Param("exclude") Set<Long> exclude);	
	
	
	@Query( "select es " +
			"from Estat e " +
			"	join e.expedientTipus et with et.id = :expedientTipusId, " +
			"	Estat es " +
			"where " +
			"	es.codi = e.codi " +
			" 	and es.expedientTipus.id = et.expedientTipusPare.id ")
	List<Estat> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);	
}
