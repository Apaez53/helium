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
	
	public List<Estat> findByExpedientTipusId(Long expedientTipusId);

	public Estat findByExpedientTipusAndCodi(
			ExpedientTipus expedientTipus,
			String codi);

	public Estat findByExpedientTipusAndId(
			ExpedientTipus expedientTipus,
			Long id);

	public Estat findByExpedientTipusIdAndCodi(
			Long expedientTipusId,
			String codi);

	public Estat findById(Long id);

	public List<Estat> findByExpedientTipusId(Long expedientTipusId, Pageable springDataPageable);
	
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
	
	@Query( "select e.codi " +
			"from Estat e " +
			"where e.expedientTipus.id = :expedientTipusId ")
	public List<String> findCodis(@Param("expedientTipusId") Long expedientTipusId);

	@Query("select e.id " +
		   "from Estat e " +
		   "where e.expedientTipus.id = :expedientTipusId " +
		   "	and e.codi in :codis")
	public List<Long> getIds(
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("codis") Set<String> codis);		
}
